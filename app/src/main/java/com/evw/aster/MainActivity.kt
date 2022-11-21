package com.evw.aster
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate


class MainActivity : AppCompatActivity() {
    lateinit var button: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        val mPrefs:SharedPreferences = getSharedPreferences("THEME", 0)
        var theme_boolean:Boolean = mPrefs.getBoolean("theme_boolean", true)
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO || theme_boolean){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            setTheme(R.style.Theme_Astergen2)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            setTheme(R.style.Theme_Astergen2_night)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button = findViewById(R.id.button1)
        button.setOnClickListener {
            if (theme_boolean) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                setTheme(R.style.Theme_Astergen2_night)
                theme_boolean = false
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                setTheme(R.style.Theme_Astergen2)
                theme_boolean = true
            }
            val editor:SharedPreferences.Editor =  mPrefs.edit()
            editor.putBoolean("theme_boolean", theme_boolean).commit()
        }

    }
    private fun reset() {
        this.recreate()
    }

}



