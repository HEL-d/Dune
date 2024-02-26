package com.evw.aster


import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebStorage
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import java.io.File


class Avatarbuilderactivity : AppCompatActivity() {

    interface WebViewCallback {
        fun onAvatarExported(avatarUrl: String)
        fun onOnUserSet(userId: String)
        fun onOnUserUpdated(userId: String)
        fun onOnUserAuthorized(userId: String)
        fun onAssetUnlock(assetRecord: WebViewInterface.AssetRecord)
        fun onUserLogout()
    }





    companion object {
        private const val ID_KEY = "id"
        private const val ASSET_ID_KEY = "assetId"
        const val CLEAR_BROWSER_CACHE = "clear_browser_cache"
        const val URL_KEY = "url_key"
        var callback: WebViewCallback? = null

        fun setWebViewCallback(callback: WebViewCallback) {
            this.callback = callback
        }





        //const val IS_CREATE_NEW = "is create new"
    }
    private var isCreateNew = false
    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    lateinit var webView: WebView
    private var webViewUrl: String = ""
    lateinit var progressBar: MaterialNeonProgressBar
    var currentTime: Long? = null
 //   var profileavatarupdator:String? = null
  //  var username:String? = null
 //   var phonenumber:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       isCreateNew = intent.getBooleanExtra(CLEAR_BROWSER_CACHE, false)
        webViewUrl = intent.getStringExtra(URL_KEY) ?: "https://demo.readyplayer.me/avatar"
        setContentView(R.layout.activity_avatarbuilderactivity)
         supportActionBar?.setDisplayHomeAsUpEnabled(true)
        webView = findViewById(R.id.webview)
        progressBar = findViewById(R.id.psg)

   //     profileavatarupdator = intent.getStringExtra("avatarbool")
     //   username = intent.getStringExtra("username")
      //  phonenumber = intent.getStringExtra("phonenumber")
      //  username = intent.getStringExtra("username")
        setUpWebView(intent.getBooleanExtra(CLEAR_BROWSER_CACHE, false))
        setUpWebViewClient()



    }

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    private  fun setUpWebView(clearBrowserCache: Boolean) {
        Log.d("RPM", "onCreate: clearBrowserCache $clearBrowserCache")
        //    Log.i("WEBVIEWACTIVITY", "onCreate: isCreateNew $isCreateNew")
        webView.settings.apply {
            javaScriptEnabled = true
            cacheMode = WebSettings.LOAD_DEFAULT
            databaseEnabled = true
            domStorageEnabled = true
            allowFileAccess = true
        }


        with(webView){
            this.addJavascriptInterface(WebViewInterface(this@Avatarbuilderactivity){ webMessage ->
                handleWebMessage(webMessage)
            },"WebView")

            if (clearBrowserCache){
                clearWebViewData()
            }
            loadUrl(webViewUrl)

        }




        /*       if (isCreateNew){
                   webView.clearHistory()
                   webView.clearFormData()
                   webView.clearCache(true)
                   CookieManager.getInstance().removeAllCookies(null)
                   CookieManager.getInstance().removeSessionCookies(null)
                   CookieManager.getInstance().flush()
                   WebStorage.getInstance().deleteAllData()
               }*/
        /* val url = "https://${ getString(R.string.partner_subdomain) }.readyplayer.me/avatar?frameApi";
         webView.loadUrl(url)*/





    }





    private fun setUpWebViewClient() {
        webView.webViewClient = object: WebViewClient(){
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            /*    handleAvatarCreated()
                if (CookieManager.getInstance().hasCookies())
                    CookieHelper(this@Avatarbuilderactivity).setUpdateState(true)*/
                progressBar.visibility = View.GONE
                webView.visibility = View.VISIBLE
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                executeJavascript()
                if (
                    CookieManager.getInstance().hasCookies()
                ) CookieHelper(this@Avatarbuilderactivity).setUpdateState(true)
            }

        }


        webView.webChromeClient = object: WebChromeClient(){
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                this@Avatarbuilderactivity.filePathCallback = filePathCallback
                fileChooserParams.let {
                    if (it!!.isCaptureEnabled){
                        if (haspermissionAccess()){
                            openCameraResultContract.launch(null)
                        } else {
                            requestPermission.launch(arrayOf(android.Manifest.permission.CAMERA,android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
                        }
                    } else {
                        openDocumentContract.launch("image/*")
                    }
                }
                return true

            }

            override fun onPermissionRequest(request: PermissionRequest?) {
                Log.d("PERMISSION", "onPermissionRequest: ${request?.resources} ")
                request?.grant(arrayOf(android.Manifest.permission.CAMERA))

            }



        }



    }

    private fun executeJavascript() {
        webView.evaluateJavascript("""
                var hasSentPostMessage = false;
                function subscribe(event) {
                    const json = parse(event);
                    const source = json.source;
                    
                    if (source !== 'readyplayerme') {
                      return;
                    }
                    
                    if (json.eventName === 'v1.frame.ready' && !hasSentPostMessage) {
                        window.postMessage(
                            JSON.stringify({
                                target: 'readyplayerme',
                                type: 'subscribe',
                                eventName: 'v1.**'
                            }),
                            '*'
                        );
                        hasSentPostMessage = true;
                    }

                    WebView.receiveData(event.data)
                }

                function parse(event) {
                    try {
                        return JSON.parse(event.data);
                    } catch (error) {
                        return null;
                    }
                }

                window.removeEventListener('message', subscribe);
                window.addEventListener('message', subscribe);
            """.trimIndent(), null)
    }


    private val openCameraResultContract  = registerForActivityResult(ActivityResultContracts.TakePicturePreview()){ bitmap->
        bitmap?.let {
            Log.d("ON RESULT", "no data bitmap: ${it}")


            //   val path = MediaStore.Images.Media.insertImage(contentResolver, it, "fromCamera.jpeg", null)

           /* val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.TITLE, File.separator + "IMG_" + System.currentTimeMillis() + ".png")
                put(MediaStore.Images.Media.DESCRIPTION,"")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
                put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
            val resolver = applicationContext.contentResolver
            val imageuri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues)
            imageuri?.let {
                resolver.openOutputStream(it).use {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, it!!)
                }
            }
            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            imageuri?.also {
                resolver.update(it,contentValues,null,null)
            }
            filePathCallback?.onReceiveValue(arrayOf(Uri.parse(imageuri.toString())))*/
        } ?: Toast.makeText(this, "No Image captured !!", Toast.LENGTH_SHORT).show()
    }


    private val openDocumentContract = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ){
        it?.let {
            filePathCallback?.onReceiveValue(arrayOf(it))
        } ?: Toast.makeText(this, "No Image Selected !!", Toast.LENGTH_SHORT).show()
    }

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ){ permissionMap ->
        if (permissionMap.values.any { it }){
            openCameraResultContract.launch(null)
        } else {
            Toast.makeText(this, "Camera permission is not granted.", Toast.LENGTH_SHORT).show()
        }

    }

    private fun haspermissionAccess(): Boolean{
        return arrayOf(android.Manifest.permission.CAMERA,android.Manifest.permission.WRITE_EXTERNAL_STORAGE).all {
            ActivityCompat.checkSelfPermission(this,it) == PackageManager.PERMISSION_GRANTED
        }
    }






  /*  private fun handleAvatarCreated() {
        webView.evaluateJavascript("""
                function subscribe(event) {
                    // post message v1, this will be deprecated
                    if(event.data.endsWith('.glb')) {
                        document.querySelector(".content").remove();
                        WebView.receiveData(event.data)
                    }
                    // post message v2
                    else {
                        const json = parse(event);
                        const source = json.source;
                        
                        if (source !== 'readyplayerme') {
                          return;
                        }
    
                        document.querySelector(".content").remove();
                        WebView.receiveData(event.data)
                    }
                }

                function parse(event) {
                    try {
                        return JSON.parse(event.data);
                    } catch (error) {
                        return null;
                    }
                }
    
                window.postMessage(
                    JSON.stringify({
                        target: 'readyplayerme',
                        type: 'subscribe',
                        eventName: 'v1.**'
                    }),
                    '*'
                );

                window.removeEventListener('message', subscribe);
                window.addEventListener('message', subscribe);
            """.trimIndent(), null)



    }*/



    private fun handleWebMessage(webMessage: WebViewInterface.WebMessage) {

        when (webMessage.eventName) {
            WebViewInterface.WebViewEvents.USER_SET -> {
                val userId = requireNotNull(webMessage.data[ID_KEY]) {
                    "RPM: 'userId' cannot be null"
                }
                callback?.onOnUserSet(userId)
            }
            WebViewInterface.WebViewEvents.USER_UPDATED -> {
                val userId = requireNotNull(webMessage.data[ID_KEY]) {
                    "RPM: 'userId' cannot be null webMessage.data"
                }
                callback?.onOnUserUpdated(userId)
            }
            WebViewInterface.WebViewEvents.USER_AUTHORIZED -> {
                val userId = requireNotNull(webMessage.data[ID_KEY]) {
                    "RPM: 'userId' cannot be null webMessage.data"
                }
                callback?.onOnUserAuthorized(userId)
            }
            WebViewInterface.WebViewEvents.ASSET_UNLOCK -> {
                val userId = requireNotNull(webMessage.data[ID_KEY]) {
                    "RPM: 'id' cannot be null webMessage.data"
                }
                val assetId = requireNotNull(webMessage.data[ASSET_ID_KEY]) {
                    "RPM: 'assetId' cannot be null webMessage.data"
                }
                var assetRecord = WebViewInterface.AssetRecord(userId, assetId)
                callback?.onAssetUnlock(assetRecord)
            }
            WebViewInterface.WebViewEvents.AVATAR_EXPORT -> {
                val avatarUrl = requireNotNull(webMessage.data["url"]) {
                    "RPM: 'url' cannot be null in webMessage.data"
                    finishActivityWithFailure("RPM: avatar 'url' property not found in event data")
                }
                callback?.onAvatarExported(avatarUrl)
                finishActivityWithResult()
            }
            WebViewInterface.WebViewEvents.USER_LOGOUT -> {
                callback?.onUserLogout()
            }
        }
    }




    fun WebView.clearWebViewData() {
        clearHistory()
        clearFormData()
        clearCache(true)
        CookieManager.getInstance().removeAllCookies(null)
        CookieManager.getInstance().removeSessionCookies(null)
        CookieManager.getInstance().flush()
        WebStorage.getInstance().deleteAllData()
    }


    private fun finishActivityWithResult() {
        val resultString = "Avatar Created Successfully"

        val data = Intent()
        data.putExtra("result_key", resultString)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    private fun finishActivityWithFailure(errorMessage: String) {
        val data = Intent()
        data.putExtra("error_key", errorMessage)
        setResult(Activity.RESULT_CANCELED, data)
        finish()
    }




}