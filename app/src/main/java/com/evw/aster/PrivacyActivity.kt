package com.evw.aster

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RelativeLayout

class PrivacyActivity : AppCompatActivity() {
    lateinit var relativeLayout: RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy)
        relativeLayout = findViewById(R.id.relativeLayout2)
        relativeLayout.setOnClickListener {
            this.finish()
        }
    }
}