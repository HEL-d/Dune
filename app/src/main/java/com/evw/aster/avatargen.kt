package com.evw.aster

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.smb.glowbutton.GlowButton


class avatargen : AppCompatActivity(),Avatarbuilderactivity.WebViewCallback {
    lateinit var button1: GlowButton
    lateinit var button2: GlowButton
    lateinit var textView: TextView
    lateinit var mytext:TextView
    var profileavatarupdator:String? = null
    var username:String? = null
    var phonenumber:String? = null
    private var urlConfig: UrlConfig = UrlConfig()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_avatargen)
        button1 = findViewById(R.id.create_avatar)
        button2 = findViewById(R.id.update_avatar)
        textView = findViewById(R.id.skip_text)
        mytext = findViewById(R.id.my_text)
        profileavatarupdator = intent.getStringExtra("avatarbool")
        phonenumber = intent.getStringExtra("phonenumber")
        username = intent.getStringExtra("username")
        Avatarbuilderactivity.setWebViewCallback(this)
        if (CookieHelper(this).getUpdateState()){
            button2.visibility = View.VISIBLE
            mytext.visibility = View.VISIBLE
        }
        button1.setOnClickListener {
            openWebViewPage(false)
        }
        button2.setOnClickListener {
            openWebViewPage(true)
        }


        textView.setOnClickListener {
            val intent:Intent = Intent(this,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("fc","token")
            startActivity(intent)
            finish()
        }



    }

//    private fun openUpdateWebView() {
//        val intent = Intent(this, Avatarbuilderactivity::class.java)
//        intent.putExtra(Avatarbuilderactivity.IS_CREATE_NEW, false)
//        intent.putExtra("avatarbool",profileavatarupdator)
//        intent.putExtra("username",username)
//        intent.putExtra("phonenumber",phonenumber)
//        startActivity(intent)
//    }

    private fun openWebViewPage(clearBrowserCache: Boolean) {
        val intent = Intent(this, Avatarbuilderactivity::class.java)
        intent.putExtra(Avatarbuilderactivity.CLEAR_BROWSER_CACHE,clearBrowserCache)
        intent.putExtra(Avatarbuilderactivity.URL_KEY, UrlBuilder(urlConfig).buildUrl())
        Avatarbuilderresultlauncher.launch(intent)
    }

    private val  Avatarbuilderresultlauncher  = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Log.d("RPM", "Result activity run.")
        }
    }

    override fun onAvatarExported(avatarUrl: String) {
        val profilepic:String = avatarUrl.replace(".glb",".png?size=230")
       if (profileavatarupdator == "true"){
           val updateavatardilog = Updateavatardilog()
           updateavatardilog.takedilaog(this,avatarUrl,profilepic,phonenumber!!,username!!)
       }else if (profileavatarupdator == "false"){
           val welcomedialog = Welcomedialog()
           welcomedialog.givedilaog(this,avatarUrl,profilepic,phonenumber!!,username!!)
       }


    }

    override fun onOnUserSet(userId: String) {

    }

    override fun onOnUserUpdated(userId: String) {

    }

    override fun onOnUserAuthorized(userId: String) {

    }

    override fun onAssetUnlock(assetRecord: WebViewInterface.AssetRecord) {

    }

    override fun onUserLogout() {

    }

    private fun showAlert(url: String){
        val context = this@avatargen
        val clipboardData = ClipData.newPlainText("Ready Player Me", url)
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.setPrimaryClip(clipboardData)
        Toast.makeText(context, "Url copied into clipboard.", Toast.LENGTH_SHORT).show()

        // display modal window with the avatar url
        val builder = AlertDialog.Builder(context).apply {
            setTitle("Result")
            setMessage(url)
            setPositiveButton("Ok"){ dialog, _ ->
                dialog.dismiss()
            }
        }.create()
        builder.show()
    }



}