package com.evw.aster

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RelativeLayout

class NotificationActivity : AppCompatActivity() {
    lateinit var relativeLayout: RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        relativeLayout = findViewById(R.id.relativeLayout2)
        relativeLayout.setOnClickListener {
            this.finish()
        }
    }
}