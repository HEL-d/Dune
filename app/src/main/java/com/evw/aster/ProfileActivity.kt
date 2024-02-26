package com.evw.aster
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ProfileActivity : AppCompatActivity(),Avatarbuilderactivity.WebViewCallback {

   lateinit var relativeLayout1: RelativeLayout
   lateinit var relativeLayout2: RelativeLayout
   lateinit var webView: WebView
   lateinit var textView: TextView
   lateinit var button: Button
   var uid = FirebaseAuth.getInstance().currentUser?.uid
   lateinit var progressBar: MaterialNeonProgressBar
    lateinit var usernametext:TextView
    lateinit var nametext:TextView
    lateinit var imageView: ImageView
    var username:String? = null
    var phoneNumber:String? = null
    var url:String? = null
    var user:String? = null
    lateinit var bioText:TextView
    lateinit var detailstext:TextView
    var postername:String? = null
    private var urlConfig: UrlConfig = UrlConfig()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        relativeLayout1 = findViewById(R.id.pav)
        relativeLayout2 = findViewById(R.id.nkl)
        webView = findViewById(R.id.webview)
        textView = findViewById(R.id.no_avatra)
        bioText = findViewById(R.id.bio_profile)
        detailstext = findViewById(R.id.details)
        usernametext = findViewById(R.id.username_profile)
         nametext = findViewById(R.id.name_profile)
        imageView = findViewById(R.id.profile_view)
        button = findViewById(R.id.no_button)
        progressBar = findViewById(R.id.profile_bar)
        lifecycleScope.launch(Dispatchers.IO){
            withContext(Dispatchers.Main){
                progressBar.visibility = View.VISIBLE
            }
            geturl().collect{
                val ur = it.get("avatarurl")
                val bio = it.get("bio")
                url = ur.toString()
                val imagesrc = it.get("profilepic")
                username = it.get("username").toString()
                val name = it.get("name")
                val no = it.get("phoneNumber")
                phoneNumber = no.toString().substring(3)
                withContext(Dispatchers.Main) {
                    if (ur == null) {
                        progressBar.visibility = View.GONE
                        textView.visibility = View.VISIBLE
                        button.visibility = View.VISIBLE
                        user = ""
                        postername = ""
                    } else {
                        postername = "file:///android_asset/poster.svg"
                        user = url!!.replace(".glb",".glb?quality=medium")
                    }

                    if (bio == null){
                        bioText.text = "Add a bio"
                    } else if(bio == ""){
                        bioText.text = "Add a bio"
                    } else {
                        bioText.text = bio.toString()
                    }
                    if (imagesrc == null) {
                        imageView.setImageResource(R.drawable.blankprofile)
                    } else {
                        Picasso.get().load(imagesrc.toString()).placeholder(R.drawable.blankprofile).into(imageView)
                    }
                    usernametext.text = username.toString()
                    nametext.text = name.toString()
                }
            }
        }
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = true
        }
        webView.loadUrl("file:///android_asset/Modelview.html")
        webView.webViewClient = object: WebViewClient(){
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                webView.evaluateJavascript("javascript: " +"updateFrompos(\"" + postername + "\")",null)
                webView.evaluateJavascript("javascript: " +"updateFromNative(\"" + user + "\")",null)
                progressBar.visibility = View.GONE
            }
        }
        relativeLayout1.setOnClickListener {
            startActivity(Intent(this,SettingActivity::class.java))
        }
        relativeLayout2.setOnClickListener {
           val profileeditnow = profileeditnow()
            profileeditnow.givedilaog(this,username,phoneNumber)
        }
        detailstext.setOnClickListener {
        supportFragmentManager.beginTransaction().replace(R.id.containerhere,friendListFragment()).addToBackStack(null).commit()
        }
        bioText.setOnClickListener {
            startActivity(Intent(this,EditActivity::class.java))
        }


        button.setOnClickListener {
            val intent = Intent(this, Avatarbuilderactivity::class.java)
            intent.putExtra(Avatarbuilderactivity.CLEAR_BROWSER_CACHE, true)
            intent.putExtra(Avatarbuilderactivity.CLEAR_BROWSER_CACHE,true)
            intent.putExtra(Avatarbuilderactivity.URL_KEY, UrlBuilder(urlConfig).buildUrl())
           val  Avatarbuilderresultlauncher  = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    Log.d("RPM", "Result activity run.")
                }
            }
            Avatarbuilderresultlauncher.launch(intent)


        }






    }


    override fun onAvatarExported(avatarUrl: String) {
        val profilepic:String = avatarUrl.replace(".glb",".png?size=230")
             val updateavatardilog = Updateavatardilog()
            updateavatardilog.takedilaog(this,avatarUrl,profilepic,phoneNumber!!,username!!)



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
        val context = this@ProfileActivity
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





    private suspend fun geturl() : Flow<DocumentSnapshot> = callbackFlow {
        val listener = object : EventListener<DocumentSnapshot> {
            override fun onEvent(snapshot: DocumentSnapshot?, exception: FirebaseFirestoreException?) {
                if (exception != null) {
                    cancel()
                    return
                }
                if (snapshot != null && snapshot.exists() ) {
                   val document = snapshot
                    trySend(document)

                }else {
                   // do nothing
                }
            }
        }
        val registration = FirebaseFirestore.getInstance().collection("Users").document(uid.toString()).addSnapshotListener(listener)
        awaitClose { registration.remove() }
    }




    /*
    include ':unityLibrary'
project(':unityLibrary').projectDir=new File('C:\\Users\\sonu computer\\astro\\unityLibrary')

        flatDir {
            dirs "${project(':unityLibrary').projectDir}/libs"
        }

            implementation project(':unityLibrary')
    implementation fileTree(dir: project(':unityLibrary').getProjectDir().toString() + ('\\libs'), include: ['*.jar'])






        private suspend fun geturl(): DocumentSnapshot? {
            withContext(Dispatchers.Main){
                progressBar.visibility = View.VISIBLE
            }
            return try {
                val doc = FirebaseFirestore.getInstance().collection("Users").document(uid.toString()).get().await()
               val ur = doc.get("avatarurl")
                val bio = doc.get("bio")
                url = ur.toString()
                val imagesrc = doc.get("profilepic")
                username = doc.get("username").toString()
                val name = doc.get("name")
                val no = doc.get("phoneNumber")
                 phoneNumber = no.toString().substring(3)

                withContext(Dispatchers.Main) {
                    if (ur == null) {
                        progressBar.visibility = View.GONE
                        textView.visibility = View.VISIBLE
                        button.visibility = View.VISIBLE
                        user = ""
                    } else {
                        user = url!!.replace(".glb",".glb?quality=medium")
                    }

                    if (bio == null){
                        bioText.text = "Add a bio"
                    } else if(bio == ""){
                        bioText.text = "Add a bio"
                    } else {
                        bioText.text = bio.toString()
                    }
                    if (imagesrc == null) {
                        imageView.setImageResource(R.drawable.blankprofile)
                    } else {
                      Picasso.get().load(imagesrc.toString()).placeholder(R.drawable.blankprofile).into(imageView)
                    }
                    usernametext.text = username.toString()
                    nametext.text = name.toString()
                }

                doc
            }catch (e:Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(this@ProfileActivity, "Nothing found", Toast.LENGTH_SHORT).show()
                }
                null
            }
        }
    */




}









