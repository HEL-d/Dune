package com.evw.aster

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.FirebaseAuth

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashcreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashcreen.setKeepOnScreenCondition{
            val mAuth = FirebaseAuth.getInstance()
            val muser = mAuth.currentUser
            if (muser != null) {
                val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                startActivity(intent)
                 finish()
            } else {
                val intent = Intent(this@SplashScreenActivity, EntranceActivity::class.java)
                startActivity(intent)
                 finish()
            }
            false

        }



    }

    private fun initScreen() {
        val mAuth = FirebaseAuth.getInstance()
        val muser = mAuth.currentUser
        if (muser != null) {
            val intent = Intent(this@SplashScreenActivity, ProfileActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this@SplashScreenActivity, EntranceActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}