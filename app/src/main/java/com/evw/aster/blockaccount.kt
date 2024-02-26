package com.evw.aster

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class blockaccount : AppCompatActivity() {
    lateinit var textView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blockaccount)
        textView = findViewById(R.id.learn_more)
        textView.setOnClickListener {
            val uri = Uri.parse("https://asterhere.blogspot.com/2023/10/frequently-asked-questionfaq.html")
            val intent = Intent(Intent.ACTION_VIEW,uri)
            startActivity(intent)
        }





    }
}