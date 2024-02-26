package com.evw.aster

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.RelativeLayout

class Helpactivity : AppCompatActivity() {
    lateinit var relativeLayout: RelativeLayout
    lateinit var linearLayout: LinearLayout
    lateinit var linearLayout2: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_helpactivity)
        relativeLayout = findViewById(R.id.relativeLayout2)
        linearLayout = findViewById(R.id.volvo)
        linearLayout2 = findViewById(R.id.call)
        relativeLayout.setOnClickListener {
            this.finish()
        }
        linearLayout.setOnClickListener {
         startActivity(Intent(this,ProblemstatmentActivity::class.java))

        }

        linearLayout2.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.setData(Uri.parse("tel:" + "7354483195"))
            startActivity(intent)
        }




    }
}