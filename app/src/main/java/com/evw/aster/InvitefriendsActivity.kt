package com.evw.aster

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity


class InvitefriendsActivity : AppCompatActivity() {
    lateinit var relativeLayout: RelativeLayout
    lateinit var linearLayout: LinearLayout
    lateinit var linearLayout2: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invitefriends)
        relativeLayout = findViewById(R.id.relativeLayout2)
        linearLayout = findViewById(R.id.volvo)
        linearLayout2 = findViewById(R.id.inv)
        relativeLayout.setOnClickListener {
            this.finish()
        }
        linearLayout2.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "plain/text"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("asterbb8.inc@gmail.com"))
            intent.putExtra(Intent.EXTRA_SUBJECT, "ddd")
            intent.putExtra(Intent.EXTRA_TEXT, "ddd")
            startActivity(Intent.createChooser(intent, "ddd"))
        }

        linearLayout.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + "asterbb8.inc@gmail.com"))
            intent.putExtra(Intent.EXTRA_SUBJECT, "your_subject")
            intent.putExtra(Intent.EXTRA_TEXT, "your_text")
            startActivity(intent)
        }


    }


}