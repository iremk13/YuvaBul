package com.dell.yuvabul

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.dell.yuvabul.adapter.UserAdapter
import com.dell.yuvabul.model.User
import com.dell.yuvabul.yuvabul.YuvaBulService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_users.*

class UsersActivity : AppCompatActivity() {
    var userList=ArrayList<User>()
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)
        auth= FirebaseAuth.getInstance()


        userRecyclerView.layoutManager=LinearLayoutManager(this,LinearLayout.VERTICAL,false)

        imgBack.setOnClickListener {
            onBackPressed()
        }
        imgProfile.setOnClickListener {
            val intent= Intent(this@UsersActivity, ProfileActivity::class.java)
            startActivity(intent)
        }
        getUsersList()

    }

    fun getUsersList(){
        val firebase:FirebaseUser=FirebaseAuth.getInstance().currentUser!!
        var UserId=firebase.uid
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/$UserId")
        val databaseReference:DatabaseReference=FirebaseDatabase.getInstance().getReference("Users")
        databaseReference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                val currentUser=snapshot.getValue(User::class.java)
                if(currentUser!!.profileImage == ""){
                    imgProfile.setImageResource(R.drawable.desse)
                }else{
                    Glide.with(this@UsersActivity).load(currentUser.profileImage).into(imgProfile)
                }
                for(dataSnapShot:DataSnapshot in snapshot.children){
                    val user= dataSnapShot.getValue(User::class.java)
                    if(!user!!.UserId.equals(firebase.uid)){
                        userList.add(user)
                    }
                }
                val userAdapter= UserAdapter(this@UsersActivity,userList)
                userRecyclerView.adapter=userAdapter
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,error.message,Toast.LENGTH_SHORT).show()
            }
        })
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean { //inflater xmlleri kodla bağlamak menuinflater da menuyle xml bağlamak içindir
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.secenekler_menusu, menu) //artık menumuz feed activitesi ile bağlandı
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.kullanici) {
            //ilan eklenecek
            val intent = Intent(this, UsersActivity::class.java)
            startActivity(intent)

        }else if(item.itemId == R.id.profil){
            val intent=Intent(this,ProfileActivity::class.java)
            startActivity(intent)
        }else if(item.itemId == R.id.ilanekle){
            val intent=Intent(this, ilanekle::class.java)
            startActivity(intent)}
        else if(item.itemId == R.id.cikisyap){
            //önce firebaseden cıkıs yapmak lazım
            auth.signOut()
            val intent = Intent(this,LoginActivity::class.java) // cıkıs yaparsak bizi girişe yönlendirecek
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}