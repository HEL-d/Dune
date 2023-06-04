package com.evw.aster

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.FirebaseAuth

class SplashScreenActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen: SplashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        initScreen()
    }

    private fun initScreen() {
        val mAuth = FirebaseAuth.getInstance()
        val muser = mAuth.currentUser
        if (muser != null) {
            val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this@SplashScreenActivity, EntranceActivity::class.java)
            startActivity(intent)
        }
        finish()
    }
}