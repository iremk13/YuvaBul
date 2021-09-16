package com.dell.yuvabul

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.dell.yuvabul.adapter.ChatAdapter
import com.dell.yuvabul.model.Chat
import com.dell.yuvabul.model.NotificationData
import com.dell.yuvabul.model.PushNotification
import com.dell.yuvabul.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_chat.imgBack
import kotlinx.android.synthetic.main.activity_chat.imgProfile
import kotlinx.android.synthetic.main.activity_chat.chatRecyclerView
import kotlinx.android.synthetic.main.activity_users.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

//import kotlinx.android.synthetic.main.activity_users.*
//import kotlinx.android.synthetic.main.activity_users.imgProfile

class ChatActivity : AppCompatActivity() {
    var firebaseUser: FirebaseUser? = null
    var reference:DatabaseReference?=null
    var chatList=ArrayList<Chat>()
    var topic=""
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        auth= FirebaseAuth.getInstance()

        chatRecyclerView.layoutManager= LinearLayoutManager(this, LinearLayout.VERTICAL,false)


        var intent=getIntent()
        var UserId=intent.getStringExtra("UserId")
        var UserName=intent.getStringExtra("UserName")

        imgBack.setOnClickListener {
            onBackPressed()
        }

        firebaseUser= FirebaseAuth.getInstance().currentUser
        reference=FirebaseDatabase.getInstance().getReference("Users").child(UserId!!)

        imgBack.setOnClickListener {
            onBackPressed()
        }

        reference!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user= snapshot.getValue(User::class.java)
                tvUserName.text=user!!.UserName
                if(user.profileImage == ""){
                    imgProfile.setImageResource(R.drawable.desse)
                }else{
                    Glide.with(this@ChatActivity).load(user.profileImage).into(imgProfile)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        btnSendMessage.setOnClickListener {
            var message:String= etMessage.text.toString()
            if(message.isEmpty()){
                Toast.makeText(applicationContext,"The message is empty.",Toast.LENGTH_SHORT).show()
                etMessage.setText("")
            }else{
                sendMessage(firebaseUser!!.uid,UserId,message)
                etMessage.setText("")
                topic="/topics/$UserId"
                PushNotification(NotificationData(UserName!!,message),topic)
                    .also {
                        sendNotification(it)
                    }

            }
        }

        readMessage(firebaseUser!!.uid,UserId)

    }

    //send message funct olustur
    private fun sendMessage(senderId:String,receiverId:String,message:String){
        var reference:DatabaseReference?=FirebaseDatabase.getInstance().getReference()
        var hashMap:HashMap<String,String> = HashMap()
        hashMap.put("senderId",senderId)
        hashMap.put("receiverId",receiverId)
        hashMap.put("message",message)

        reference!!.child("Chat").push().setValue(hashMap)


    }

    fun readMessage(senderId: String,receiverId: String){
        val databaseReference:DatabaseReference=
            FirebaseDatabase.getInstance().getReference("Chat")

        databaseReference.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for(dataSnapShot:DataSnapshot in snapshot.children){
                    val chat= dataSnapShot.getValue(Chat::class.java)

                    if(chat!!.senderId.equals(senderId) && chat!!.receiverId.equals(receiverId) ||
                        chat!!.senderId.equals(receiverId) && chat!!.receiverId.equals(senderId)){
                        chatList.add(chat)
                    }

                }

                val chatAdapter= ChatAdapter(this@ChatActivity,chatList)

                chatRecyclerView.adapter=chatAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }
    private fun sendNotification(notification:PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d("TAG", "Response: ${Gson().toJson(response)}")
            } else {
                Log.e("TAG", response.errorBody()!!.string())
            }
        } catch(e: Exception) {
            Log.e("TAG", e.toString())
        }
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

        } else if (item.itemId == R.id.profil) {
            val intent = Intent(this, ProfileActivity::class.java)
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