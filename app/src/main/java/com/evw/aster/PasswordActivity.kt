package com.evw.aster
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.smb.glowbutton.GlowButton

class PasswordActivity : AppCompatActivity() {
    lateinit var viewDialog: ViewDialog
    lateinit var networkbox: Networkbox
    lateinit var internetConnectivity: InternetConnectivity
    lateinit var button: GlowButton
    lateinit var editText: TextInputEditText
    lateinit var name:String
    lateinit var birthday:String
    lateinit var username:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)
        button = findViewById(R.id.button_pass)
        editText = findViewById(R.id.edittext_pass)
        internetConnectivity = InternetConnectivity()
        viewDialog = ViewDialog()
        networkbox = Networkbox()
        name = intent.getStringExtra("name").toString()
        birthday = intent.getStringExtra("birthday").toString()
        username = intent.getStringExtra("username").toString()
        button.setOnClickListener {
            if (editText.text?.isEmpty() == true || editText.text?.length!! <= 5) {
                viewDialog.showDialog(this, "Password is required to proceed", "password should be mix of letter and char")
            } else {
             /*   if (internetConnectivity.isNetworkAvailable(this)) {
                    val intent: Intent = Intent(this,LoginActivity::class.java)
                    intent.putExtra("name",name)
                    intent.putExtra("birthday",birthday)
                    intent.putExtra("username",username)
                    intent.putExtra("password",editText.text.toString())
                    startActivity(intent)
                } else {
                    networkbox.shownetworkdialog(this)
                }*/
            }
        }
    }


}


