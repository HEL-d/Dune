package com.evw.aster
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.bumptech.glide.Glide
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration

class EntranceActivity : AppCompatActivity() {
    val appid:String = "astergen2-ijwce"
    lateinit var imageView: ImageView
    val app = App(AppConfiguration.Builder(appid).build())
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entrance)
        imageView = findViewById(R.id.vvvv)



    }
}