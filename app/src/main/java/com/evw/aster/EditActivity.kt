package com.evw.aster

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.smb.glowbutton.GlowButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class EditActivity : AppCompatActivity() {
    lateinit var nameeditext:EditText
    lateinit var birthdayedittext:EditText
    lateinit var bioedittext:EditText
    lateinit var glowButton: GlowButton
    lateinit var relativeLayout: RelativeLayout
    var uid = FirebaseAuth.getInstance().currentUser?.uid
    lateinit var viewDialog: ViewDialog
    lateinit var progressbardialog:Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        nameeditext = findViewById(R.id.name_now)
        birthdayedittext = findViewById(R.id.birthday_now)
        bioedittext = findViewById(R.id.bio_now)
        glowButton = findViewById(R.id.button_save)
        relativeLayout = findViewById(R.id.relativeLayout2)
       viewDialog = ViewDialog()
        progressbardialog = Dialog(this)
        initDialog()
        lifecycleScope.launch(Dispatchers.IO){
            getDatafromdatabase()
        }
        relativeLayout.setOnClickListener {
            this.finish()
        }
        glowButton.setOnClickListener {
            if (birthdayedittext.text.isEmpty() || nameeditext.text.isEmpty()){
                    Toast.makeText(this,"Fill the full info first",Toast.LENGTH_SHORT).show()
                } else {
                     progressbardialog.show()
                    lifecycleScope.launch(Dispatchers.IO) {
                        sendData(nameeditext.text.toString(),birthdayedittext.text.toString(),bioedittext.text.toString())
                    }
                }
        }



    }

    private fun initDialog() {
        progressbardialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressbardialog.setCancelable(false)
        progressbardialog.setContentView(R.layout.progressdialog)
    }

    private suspend fun sendData(name: String, birth: String, bio: String):Boolean{
        val updates = hashMapOf<String, Any>("name" to name, "birthday" to birth,"bio" to bio)
        return try {
            val result = FirebaseFirestore.getInstance().collection("Users").document(uid.toString()).update(updates).await()
            withContext(Dispatchers.Main){
                progressbardialog.dismiss()
                Toast.makeText(this@EditActivity,"saved successfully",Toast.LENGTH_SHORT).show()
            }
            true
        } catch (e:Exception){
            withContext(Dispatchers.Main){
                viewDialog.showDialog(this@EditActivity,"Try again later to save the data","check your network connection and try")
                Toast.makeText(this@EditActivity,"Failed to save",Toast.LENGTH_SHORT).show()
            }
            false
        }
    }

    private suspend fun getDatafromdatabase():DocumentSnapshot? {
        return try {
            val doc = FirebaseFirestore.getInstance().collection("Users").document(uid.toString()).get().await()
            val dbname = doc.get("name")
            val dbbirthdate = doc.get("birthday")
            val bio = doc.get("bio")

            withContext(Dispatchers.Main){
                if (bio == null){
                    bioedittext.setText("")
                }else {
                    bioedittext.setText(bio.toString())
                }
                nameeditext.setText(dbname.toString())
                birthdayedittext.setText(dbbirthdate.toString())
            }

            doc
        } catch (e:Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@EditActivity, "Nothing found", Toast.LENGTH_SHORT).show()
            }
            null
        }
    }


}