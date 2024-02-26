package com.evw.aster

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class SettingActivity : AppCompatActivity() {
    lateinit var linearLayout: LinearLayout
    lateinit var relativeLayout: RelativeLayout
    lateinit var linearLayout2: LinearLayout
    lateinit var linearLayout3: LinearLayout
    lateinit var linearLayout4: LinearLayout
    lateinit var linearLayout5: LinearLayout
    lateinit var linearLayout6: LinearLayout
    lateinit var linearLayout7: LinearLayout
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        linearLayout = findViewById(R.id.sssg)
        relativeLayout  = findViewById(R.id.pop)
        linearLayout2 = findViewById(R.id.mmm)
        linearLayout3 = findViewById(R.id.volvo)
        linearLayout4 = findViewById(R.id.mkl)
        linearLayout5 = findViewById(R.id.por)
        linearLayout6 = findViewById(R.id.nnn)
        linearLayout7 = findViewById(R.id.mich_lin)
        linearLayout.setOnClickListener {
         startActivity(Intent(this,PrivacyActivity::class.java))
        }
        relativeLayout.setOnClickListener {
            this.finish()
        }
        linearLayout2.setOnClickListener {
            startActivity(Intent(this,NotificationActivity::class.java))
        }
        linearLayout3.setOnClickListener {
            startActivity(Intent(this,PersonalActivity::class.java))
        }
       linearLayout4.setOnClickListener {
           startActivity(Intent(this,InvitefriendsActivity::class.java))
       }
        linearLayout5.setOnClickListener {
            startActivity(Intent(this,Helpactivity::class.java))
        }
        linearLayout6.setOnClickListener {
            startActivity(Intent(this,AboutActivity::class.java))
        }

        linearLayout7.setOnClickListener {
            FirebaseFirestore.getInstance().collection("Users").document(uid.toString()).get().addOnCompleteListener {
                val url = it.result.get("avatarurl")
                val ppc = uid?.take(4)
                if (url != null){
                    val intent: Intent = Intent(Intent(this,MichActive::class.java))
                    intent.putExtra("url",url.toString())
                    intent.putExtra("ppc",ppc)
                    startActivity(intent)
                } else {
                    Toast.makeText(this,"You don't have Avatar yet,",Toast.LENGTH_LONG).show()
                }

            }



        }



    }
}