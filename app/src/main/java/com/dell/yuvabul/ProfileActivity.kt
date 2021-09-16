package com.dell.yuvabul

import android.R.attr
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dell.yuvabul.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_users.*
import kotlinx.android.synthetic.main.activity_users.imgBack
import kotlinx.android.synthetic.main.item_user.*
import java.io.IOException
import java.util.*


class ProfileActivity : AppCompatActivity() {
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth:FirebaseAuth
    private var filePath: Uri? = null
    private val PICK_IMAGE_REQUEST: Int = 2020
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        auth= FirebaseAuth.getInstance()
        //current user datasını alma
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        databaseReference =
            FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                etUserName.setText(user!!.UserName)
                if (user.profileImage == "") {
                    userImage1.setImageResource(R.drawable.desse)
                } else {
                    Glide.with(this@ProfileActivity).load(user.profileImage).into(userImage1)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }

        })

        imgBack.setOnClickListener {
            onBackPressed()
        }

        userImage1.setOnClickListener {
            chooseImage()
        }
        btnSave.setOnClickListener {
            uploadImage()
            progressBar.visibility = View.VISIBLE
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
            val intent = Intent(this, LoginActivity::class.java) // cıkıs yaparsak bizi girişe yönlendirecek
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun chooseImage() {
        val intent: Intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode != null) {
            filePath = data!!.data
            try {
                var bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                userImage1.setImageBitmap(bitmap)
                btnSave.visibility = View.VISIBLE
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    private fun uploadImage() {
        if (filePath != null) {
            var ref: StorageReference = storageRef.child("image/" + UUID.randomUUID().toString())
            ref.putFile(filePath!!).addOnSuccessListener {
                val hashMap: HashMap<String, String> = HashMap()
                hashMap.put("UserName", etUserName.text.toString())
                hashMap.put("profileImage", filePath.toString())
                databaseReference.updateChildren(hashMap as Map<String, Any>)
                progressBar.visibility = View.GONE
                Toast.makeText(applicationContext, "Uploaded.", Toast.LENGTH_SHORT).show()
                btnSave.visibility = View.GONE
            }
                .addOnFailureListener {
                    progressBar.visibility = View.GONE
                    Toast.makeText(applicationContext, "Failed." + it.message, Toast.LENGTH_SHORT)
                        .show()
                }
        }


    }
}
