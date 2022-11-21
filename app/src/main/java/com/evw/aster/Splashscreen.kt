package com.evw.aster

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class Splashscreen : AppCompatActivity() {
    val appid:String = "astergen2-ijwce"
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        setTheme(R.style.Theme_Astergen2_night)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)
//         if (currentuser!= null){
//             startActivity(Intent(this,LoginActivity::class.java))
//         } else {
//             startActivity(Intent(this,EntranceActivity::class.java))
//         }
    }
}


