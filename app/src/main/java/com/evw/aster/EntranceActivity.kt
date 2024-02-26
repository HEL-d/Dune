package com.evw.aster
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.smb.glowbutton.GlowButton


class EntranceActivity : AppCompatActivity() {
    lateinit var internetConnectivity: InternetConnectivity
    lateinit var SignUP: GlowButton
     lateinit var Login: AppCompatButton
     lateinit var networkbox: Networkbox
     lateinit var textView: TextView
     lateinit var textView2: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entrance)
         SignUP = findViewById(R.id.chip2)
         Login = findViewById(R.id.chip)
         networkbox = Networkbox()
        textView = findViewById(R.id.term)
        textView2 = findViewById(R.id.policy)
        internetConnectivity = InternetConnectivity()
         SignUP.setOnClickListener {
          internetConnectivity.checkInternetConnection(object:InternetConnectivity.ConnectivityCallback{
              override fun onDetected(isConnected: Boolean) {
                  if (isConnected){
                      startActivity(Intent(this@EntranceActivity,BasicinfoActivity::class.java))

                  } else {
                    networkbox.shownetworkdialog(this@EntranceActivity)
                  }




              }

          },this)


         }
              Login.setOnClickListener {
                 internetConnectivity.checkInternetConnection(object:InternetConnectivity.ConnectivityCallback{
                     override fun onDetected(isConnected: Boolean) {
                         if (isConnected){
                             startActivity(Intent(this@EntranceActivity,AlreadyAccount::class.java))

                         } else {
                             networkbox.shownetworkdialog(this@EntranceActivity)
                         }
                     }
                 },this)
        }

        textView.setOnClickListener {
            val uri = Uri.parse("https://asterhere.blogspot.com/2023/10/term-and-conditions.html")
            val intent = Intent(Intent.ACTION_VIEW,uri)
            startActivity(intent)
        }

        textView2.setOnClickListener {
            val uri = Uri.parse("https://asterhere.blogspot.com/2023/10/privacy-policy.html")
            val intent = Intent(Intent.ACTION_VIEW,uri)
            startActivity(intent)
        }



    }


}