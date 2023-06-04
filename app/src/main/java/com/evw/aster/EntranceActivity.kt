package com.evw.aster
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.smb.glowbutton.GlowButton


class EntranceActivity : AppCompatActivity() {
    lateinit var internetConnectivity: InternetConnectivity
    lateinit var SignUP: GlowButton
     lateinit var Login: AppCompatButton
     lateinit var networkbox: Networkbox
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entrance)
         SignUP = findViewById(R.id.chip2)
         Login = findViewById(R.id.chip)
         networkbox = Networkbox()
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





    }


}