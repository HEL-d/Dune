package com.evw.aster

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.smb.glowbutton.GlowButton

class ProblemstatmentActivity : AppCompatActivity() {
    lateinit var editText: EditText
    lateinit var glowButton: GlowButton
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    lateinit var progressbardialog: Dialog
    lateinit var cyanborderdialog: Cyanborderdialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_problemstatment)
        editText =findViewById(R.id.editText2)
        glowButton = findViewById(R.id.button_send)
        cyanborderdialog = Cyanborderdialog()
        progressbardialog = Dialog(this)
        initdialog()
        glowButton.setOnClickListener {
            if (editText.text.isEmpty()){
        Toast.makeText(this,"Empty",Toast.LENGTH_SHORT).show()
            } else {
                val map = hashMapOf<Any,Any>()
                map.put("problem",editText.text.toString())
                progressbardialog.show()
                FirebaseFirestore.getInstance().collection("pStatement").document(uid.toString()).set(map).addOnSuccessListener {
                    progressbardialog.dismiss()
                    Toast.makeText(this,"send successfully",Toast.LENGTH_SHORT).show()
                }
                editText.setText("")
            }
        }
    }

    private fun initdialog() {
        progressbardialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressbardialog.setCancelable(false)
        progressbardialog.setContentView(R.layout.progressdialog)
    }
}