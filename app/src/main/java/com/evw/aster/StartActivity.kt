package com.evw.aster

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class StartActivity : AppCompatActivity() {
    lateinit var mauth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        mauth = FirebaseAuth.getInstance()
        /*val currentuser = mauth.currentUser
          if (currentuser != null){
              startActivity(Intent(this,MainActivity::class.java))
          } else {
              startActivity(Intent(this,EntranceActivity::class.java))
          }*/
       
    }
}