package com.evw.aster

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class userpassActivity : AppCompatActivity() {
    lateinit var editText1: TextInputEditText
    lateinit var editText2: TextInputEditText
    lateinit var button: Button
    lateinit var internetConnectivity: InternetConnectivity
    lateinit var viewDialog: ViewDialog
    lateinit var networkbox: Networkbox
    lateinit var mauth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userpass)
        button = findViewById(R.id.button_save)
        editText1 = findViewById(R.id.user_name)
        editText2 = findViewById(R.id.pass_word)
        mauth = FirebaseAuth.getInstance()
        internetConnectivity = InternetConnectivity()
        viewDialog = ViewDialog()
        networkbox = Networkbox()
        button.setOnClickListener {
            if (editText1.text?.isEmpty() == true || editText2.text?.isEmpty() == true){
                viewDialog.showDialog(this,"Both fields are required to proceed","Note:fields are excluded from your profile")
            } else{
              /*  if (internetConnectivity.isNetworkAvailable(this)){
                     CoroutineScope(Dispatchers.IO).launch {
                         checkUsernameandPassword()
                     }



                }else{
                    networkbox.shownetworkdialog(this)
                }*/

            }



        }




    }





    private suspend fun checkUsernameandPassword() :DocumentSnapshot? {
        return try {
            val doc = FirebaseFirestore.getInstance().collection("Usersname").document(editText1.text.toString()).get().await()
            if (doc.exists()){
             val password:String = doc.get("password") as String
             val credential:String = doc.get("credential") as String
                updateUI(password,credential)
            }else{
                getUI()
            }
            doc
        } catch (e:Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@userpassActivity, "usernull", Toast.LENGTH_SHORT).show()
            }
            null
        }




    }

    private suspend fun updateUI(password: String, credential: String) {
        if (editText2.text.toString() == password){
         //   signIn(credential)

        } else {
            withContext(Dispatchers.Main){
                Toast.makeText(this@userpassActivity,"Wrong Password",Toast.LENGTH_SHORT).show()
            }
        }


    }




    private suspend fun UserInterface() {
       withContext(Dispatchers.Main){
            Toast.makeText(this@userpassActivity,"SignIn",Toast.LENGTH_SHORT).show()
        }
    }


    private suspend fun getUI() {
        withContext(Dispatchers.Main){
            Toast.makeText(this@userpassActivity,"wrong Username",Toast.LENGTH_SHORT).show()
        }
    }


}


