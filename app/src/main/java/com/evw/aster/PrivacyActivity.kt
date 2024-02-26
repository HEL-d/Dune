package com.evw.aster

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PrivacyActivity : AppCompatActivity() {
    lateinit var relativeLayout: RelativeLayout
    lateinit var linearLayout: LinearLayout
    lateinit var progressbardilog :Dialog
    lateinit var cyanborderdialog: Cyanborderdialog
    lateinit var linearLayout2: LinearLayout
    var uid = FirebaseAuth.getInstance().currentUser?.uid
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy)
        relativeLayout = findViewById(R.id.relativeLayout2)
        linearLayout = findViewById(R.id.ooo)
        linearLayout2 = findViewById(R.id.volvo)
        progressbardilog = Dialog(this)
        cyanborderdialog = Cyanborderdialog()
        initdialog()
        relativeLayout.setOnClickListener {
            this.finish()
        }

        linearLayout.setOnClickListener {
            progressbardilog.show()
            FirebaseFirestore.getInstance().collection("Deleteaccount").document(uid.toString()).set("delete" to true).addOnSuccessListener {
                progressbardilog.dismiss()
                cyanborderdialog.makedialog(this,"we will delete your account within","two days you can unistall the app now")
            }
        }

       linearLayout2.setOnClickListener {
           startActivity(Intent(this,blockaccount::class.java))
       }

    }

    private fun initdialog() {
        progressbardilog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressbardilog.setCancelable(false)
        progressbardilog.setContentView(R.layout.progressdialog)
    }
}