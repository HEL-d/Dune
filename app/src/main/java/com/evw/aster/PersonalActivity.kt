package com.evw.aster

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PersonalActivity : AppCompatActivity() {
    lateinit var relativeLayout: RelativeLayout
    lateinit var textView: TextView
    lateinit var textView2: TextView
    lateinit var textView3: TextView
    lateinit var textView4: TextView
    lateinit var textView5: TextView
    var uid = FirebaseAuth.getInstance().currentUser?.uid
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal)
        relativeLayout = findViewById(R.id.rper)
        textView = findViewById(R.id.edit_now)
        textView2 = findViewById(R.id.textView11)
        textView3 = findViewById(R.id.text_fullname)
        textView4 = findViewById(R.id.text_birthdate)
        textView5 = findViewById(R.id.text_phone)
        relativeLayout.setOnClickListener {
            this.finish()
        }
        textView.setOnClickListener {
            startActivity(Intent(this,EditActivity::class.java))
        }

       FirebaseFirestore.getInstance().collection("Users").document(uid.toString()).addSnapshotListener { value, error ->
          if (value != null){

              val name = value.get("name")
              val username = value.get("username")
              val birthday = value.get("birthday")
              val phoneNumber = value.get("phoneNumber")
              textView3.setText(name.toString())
              textView2.setText(username.toString())
              textView4.setText(birthday.toString())
              textView5.setText(phoneNumber.toString())


          }


       }






    }
}