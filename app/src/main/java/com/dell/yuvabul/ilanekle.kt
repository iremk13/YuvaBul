package com.dell.yuvabul

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_feed.*
import kotlinx.android.synthetic.main.activity_ilanekle.*
import kotlinx.android.synthetic.main.activity_ilanekle.imageView
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.*

class ilanekle : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var database: FirebaseFirestore

    var secilenGorsel: Uri? = null
    var secilenBitmap: Bitmap? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ilanekle)
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        database = FirebaseFirestore.getInstance()
    }

    fun ilanolustur(view: View) {
        val uuid = UUID.randomUUID()
        val gorselismi = "${uuid}.jpg"
        //önce görseli yüklemek firebase depoya gidiyor geri kalan veriler de veritabanına kaydediliyor (deponun yerini bilmemiz lazım)
        //önce depo islemleri
        val storage = FirebaseStorage.getInstance()
        val reference =
            storage.reference //görselimizi nereye kaydediceğimizi bunun sayesinde söyleyebiliyoruz
        val gorselReference = reference.child("postimages")
            .child(gorselismi) //görsel ismi yazınca hepsi üst üste yazılacak ve bu yuzden tek görsel kaydedilecek
        //UUID- universal unique id ataması olacak fotografların
        if (secilenGorsel != null) {
            gorselReference.putFile(secilenGorsel!!).addOnSuccessListener { taskSnapshot ->
                val yuklenenGorselReference =
                    FirebaseStorage.getInstance().reference.child("postimages").child(gorselismi)
                yuklenenGorselReference.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    val kullaniciadi1=kullaniciadi.text.toString()

                    val guncelkullaniciemaili = auth.currentUser!!.email.toString()
                    val kediköpek = kedikopek.text.toString()
                    if (kediköpek == null) {
                        println("Lutfen bos gecmeyin.")
                    }
                    val irk = irk!!.text.toString()

                    val yas = yas!!.text.toString()

                    val cinsiyet = cinsiyet.text.toString()


                    val il = il.text.toString()
                    if (il == null) {
                        println("Lutfen bos gecmeyin.")
                    }
                    val ilce = ilce.text.toString()
                    if (ilce == null) {
                        println("Lutfen bos gecmeyin.")
                    }
                    val barinak = barinak.text.toString()
                    if (barinak == null) {
                        println("Lutfen bos gecmeyin.")
                    }
                    val genelbilgi = genelbilgi.text.toString()
                    val tarih = com.google.firebase.Timestamp.now()

                    //println(downloadUrl)
                    //urlyi veritabanına kaydediyoruz

                    val postHashMap = hashMapOf<String, Any>()
                    postHashMap.put("gorselurl", downloadUrl)
                    postHashMap.put("kullaniciadi",kullaniciadi1)
                    postHashMap.put("kullaniciemail", guncelkullaniciemaili)
                    postHashMap.put("kediköpek", kediköpek)
                    postHashMap.put("irk", irk)
                    postHashMap.put("yas", yas)
                    postHashMap.put("cinsiyet", cinsiyet)
                    postHashMap.put("il", il)
                    postHashMap.put("ilce", ilce)
                    postHashMap.put("barinak", barinak)
                    postHashMap.put("genelbilgi", genelbilgi)
                    postHashMap.put("tarih", tarih)

                    database.collection("İlan Ekle").add(postHashMap)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                finish()
                            }
                        }.addOnFailureListener { exception ->
                            Toast.makeText(
                                applicationContext,
                                exception.localizedMessage,
                                Toast.LENGTH_LONG
                            ).show()
                        }


                }.addOnFailureListener { exception ->
                    Toast.makeText(
                        applicationContext,
                        exception.localizedMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        //listener ekle

       /*val uyarimesaji=AlertDialog.Builder(this@ilanekle)
        uyarimesaji.setTitle("Emin misiniz?")
        uyarimesaji.setMessage("İlanı yayınlamak istediğinize emin misiniz?")

        uyarimesaji.setPositiveButton("Evet",DialogInterface.OnClickListener{dialogInterface, i ->
            Toast.makeText(this,"İlan Paylaşıldı.",Toast.LENGTH_LONG).show()
        })
        uyarimesaji.setPositiveButton("Hayır",DialogInterface.OnClickListener{dialogInterface, i ->
            Toast.makeText(this,"İlan İptal Edildi.",Toast.LENGTH_LONG).show()
        })
        uyarimesaji.show()*/

    }

    fun gorselSec(view: View) {
        //izin verilmediyse
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        } else {
            //izin verildiyse
            val galeriIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galeriIntent, 2)
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //izin verilince yapılacaklar
                val galeriIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent, 2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            secilenGorsel = data.data
            if (secilenGorsel != null) {
                if (Build.VERSION.SDK_INT >= 28) {
                    val source = ImageDecoder.createSource(this.contentResolver, secilenGorsel!!)
                    secilenBitmap = ImageDecoder.decodeBitmap(source)
                    imageView.setImageBitmap(secilenBitmap)
                } else {
                    secilenBitmap =
                        MediaStore.Images.Media.getBitmap(this.contentResolver, secilenGorsel)
                    imageView.setImageBitmap(secilenBitmap)
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
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
}
