package com.evw.aster
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.smb.glowbutton.GlowButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.regex.Matcher
import java.util.regex.Pattern

class UniqueNameActivity : AppCompatActivity() {
    lateinit var internetConnectivity: InternetConnectivity
    lateinit var editText: EditText
    lateinit var button: GlowButton
    lateinit var viewDialog: ViewDialog
    lateinit var networkbox: Networkbox
    lateinit var name:String
    lateinit var birthday:String
    lateinit var progressbardialog:Dialog
    lateinit var newbox: newbox
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unique_name)
         name = intent.getStringExtra("name").toString()
         birthday = intent.getStringExtra("birthday").toString()
        internetConnectivity = InternetConnectivity()
        viewDialog = ViewDialog()
        networkbox = Networkbox()
        newbox = newbox()
        progressbardialog = Dialog(this)
        initDialog()
        editText = findViewById(R.id.username)
        button = findViewById(R.id.button_name)

        button.setOnClickListener {
            if ((editText.text?.isEmpty() == true) || (editText.text.length < 6) || (!(isValidPassword(editText.text.toString())).first) && !(isValidPassword(editText.text.toString())).second ) {
                viewDialog.showDialog(this, "username should be unique", "Use mix of atleast 6 numbers and letters")
            } else {
                internetConnectivity.checkInternetConnection(object:InternetConnectivity.ConnectivityCallback{
                    override fun onDetected(isConnected: Boolean) {
                        if (isConnected){
                            progressbardialog.show()
                            CoroutineScope(Dispatchers.IO).launch {
                                getusernames(editText.text.toString())
                            }
                        } else {
                            networkbox.shownetworkdialog(this@UniqueNameActivity)
                        }
                    }

                },this)
            }
        }

    }

    private fun initDialog() {
        progressbardialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressbardialog.setCancelable(false)
        progressbardialog.setContentView(R.layout.progressdialog)
    }

    fun isValidPassword(password: String?): Pair<Boolean,Boolean> {
        val pattern1: Pattern
        val pattern2:Pattern
        val matcher1: Matcher
        val matcher2:Matcher
        val PASSWORD_PATTERN1 = "^(?=.*[0-9])(?=\\S+$).{4,}$"
        val PASSWORD_PATTERN2 = "^(?=.*[@_.])(?=\\S+$).{4,}$"
        pattern1 = Pattern.compile(PASSWORD_PATTERN1)
        pattern2 = Pattern.compile(PASSWORD_PATTERN2)
        matcher1 = pattern1.matcher(password)
        matcher2 = pattern2.matcher(password)
        return Pair(matcher1.matches(),matcher2.matches())

    }






    private suspend fun getusernames(username: String):DocumentSnapshot? {
        return try {
            val doc = FirebaseFirestore.getInstance().collection("Usersname").document(username).get().await()
            if (doc.exists()){
                updateUI()
            }else{
               getUI(username)
            }
            doc
        } catch (e:Exception){
            withContext(Dispatchers.Main){
                newbox.shoDialog(this@UniqueNameActivity)
                Toast.makeText(this@UniqueNameActivity, "null value", Toast.LENGTH_SHORT).show()
            }
          null
        }
    }
    private suspend fun getUI(username: String) {
        withContext(Dispatchers.Main){
            progressbardialog.dismiss()
            Toast.makeText(this@UniqueNameActivity, "Congratulations", Toast.LENGTH_SHORT).show()
             val intent:Intent = Intent(this@UniqueNameActivity,LoginActivity::class.java)
             intent.putExtra("username",username)
            intent.putExtra("name",name)
            intent.putExtra("birthday",birthday)
            startActivity(intent)
        }
    }

    private suspend fun updateUI() {

       withContext(Dispatchers.Main){
           progressbardialog.dismiss()
           viewDialog.showDialog(this@UniqueNameActivity, "Username is already Taken", "You should try other Username to proceed")
           Toast.makeText(this@UniqueNameActivity, "Notaviliable", Toast.LENGTH_SHORT).show()


       }

    }
}