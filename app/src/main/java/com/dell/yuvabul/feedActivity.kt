package com.dell.yuvabul

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_feed.*
import kotlinx.android.synthetic.main.activity_ilanekle.*
import kotlinx.android.synthetic.main.activity_ilanekle.view.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.item_user.*
import kotlinx.android.synthetic.main.recycler_row.*
import java.util.*
import kotlin.collections.ArrayList




class feedActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    private lateinit var recyclerViewAdapter: feedRecyclerAdapter
    var ilanlistesi = ArrayList<ilan>()
    val displayList = ArrayList<ilan>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)



        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
        verilerial()
        var layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerViewAdapter = feedRecyclerAdapter(ilanlistesi)
        recyclerView.adapter = recyclerViewAdapter
    }


    //whwre eual to field kullaniciemail,email adresi emaile göre sıralama
    fun verilerial() {
        database.collection("İlan Ekle")
            .orderBy("tarih", Query.Direction.DESCENDING) //en son girilen tarih ilk basta cıkacak
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_LONG).show()
                } else {
                    if (snapshot != null) {
                        if (!snapshot.isEmpty) {
                            val documents = snapshot.documents
                            ilanlistesi.clear()
                            for (document in documents) {
                                /* val kullaniciadireference=FirebaseStorage.getInstance().reference.child("Users").child(
                                    UserName.toString()
                                )*/
                                val kullaniciadi = document.get("kullaniciadi") as String
                                val kullaniciemail = document.get("kullaniciemail") as String
                                val kediköpek = document.get("kediköpek") as String
                                val irk = document.get("irk") as String
                                val yas = document.get("yas") as String
                                //val cinsiyet: String? = null
                                val cinsiyet = document.get("cinsiyet") as String
                                val il = document.get("il") as String
                                val ilce = document.get("ilce") as String
                                val barinak = document.get("barinak") as String
                                val genelbilgi = document.get("genelbilgi") as String
                                val gorselurl = document.get("gorselurl") as String

                                val indirilenpost = ilan(
                                    kullaniciadi,
                                    kullaniciemail,
                                    kediköpek,
                                    irk,
                                    yas,
                                    cinsiyet,
                                    il,
                                    ilce,
                                    barinak,
                                    genelbilgi,
                                    gorselurl
                                )
                                ilanlistesi.add(indirilenpost)
                                displayList.addAll(ilanlistesi)

                            }
                            //  recyclerViewAdapter.notifyDataSetChanged()
                        }

                    }
                }

            }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean { //inflater xmlleri kodla bağlamak menuinflater da menuyle xml bağlamak içindir
        val menuInflater = menuInflater
        menuInflater.inflate(
            R.menu.secenekler_menusu,
            menu
        ) //artık menumuz feed activitesi ile bağlandı

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
            } else if (item.itemId == R.id.ilanekle) {
                val intent = Intent(this, ilanekle::class.java)
                startActivity(intent)
            } else if (item.itemId == R.id.cikisyap) {
                //önce firebaseden cıkıs yapmak lazım
                auth.signOut()
                val intent = Intent(
                    this,
                    LoginActivity::class.java
                ) // cıkıs yaparsak bizi girişe yönlendirecek
                startActivity(intent)
                finish()
            }
            return super.onOptionsItemSelected(item)
        }
    }




