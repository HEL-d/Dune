package com.evw.aster

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.RelativeLayout

class AboutActivity : AppCompatActivity() {
    lateinit var relativeLayout: RelativeLayout
    lateinit var linearLayout: LinearLayout
    lateinit var linearLayout2: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        relativeLayout = findViewById(R.id.relativeLayout2)
        relativeLayout.setOnClickListener {
            this.finish()
        }
        linearLayout = findViewById(R.id.volvo)
        linearLayout2 = findViewById(R.id.faq)
        linearLayout.setOnClickListener {
            val uri = Uri.parse("https://asterhere.blogspot.com/2023/10/privacy-policy.html")
            val intent = Intent(Intent.ACTION_VIEW,uri)
            startActivity(intent)
        }

       linearLayout2.setOnClickListener {
           val uri = Uri.parse("https://asterhere.blogspot.com/2023/10/frequently-asked-questionfaq.html")
           val intent = Intent(Intent.ACTION_VIEW,uri)
           startActivity(intent)
       }


    }
}