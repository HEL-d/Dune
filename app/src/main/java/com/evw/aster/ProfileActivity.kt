package com.evw.aster

import android.content.Intent
import android.os.Bundle
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity


class ProfileActivity : AppCompatActivity() {
   lateinit var relativeLayout1: RelativeLayout
   lateinit var relativeLayout2: RelativeLayout
   lateinit var relativeLayout3: RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        relativeLayout1 = findViewById(R.id.pav)
        relativeLayout2 = findViewById(R.id.nkl)
        relativeLayout3 = findViewById(R.id.conn)
        relativeLayout1.setOnClickListener {
            startActivity(Intent(this,SettingActivity::class.java))
        }
        relativeLayout2.setOnClickListener {
            startActivity(Intent(this,EditActivity::class.java))
        }
        relativeLayout3.setOnClickListener {
            startActivity(Intent(this,FriendsActivity::class.java))
        }
    }
}









