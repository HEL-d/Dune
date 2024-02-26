package com.evw.aster

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.webkit.JavascriptInterface
import android.widget.Toast
import org.json.JSONObject

class Webviewinterface2 (private val context: Context) {
    @JavascriptInterface
    fun receiveData(text: String){
        // extract avatar url from received message
        val url = if(text.endsWith(".glb")) text else JSONObject(text).getJSONObject("data").getString("url");
        val profilepic:String = url.replace(".glb",".png")
        // copy to clipboard
        val data = ClipData.newPlainText("Ready Player Me", url)
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.setPrimaryClip(data)
        Toast.makeText(context, "Url copied into clipboard.", Toast.LENGTH_SHORT).show()

        // display modal window with the avatar url



        /*  val builder = AlertDialog.Builder(context).apply {
                   setTitle("Result")
                   setMessage(Html.fromHtml("<font color='#FF7F27'>This is a test</font>"))
              "Successfully created"
                   setCancelable(false)
                   setPositiveButton("Ok"){ dialog, _ ->
                       FirebaseFirestore.getInstance().collection("Users").document(uid.toString()).update("avatarurl",url).addOnSuccessListener {
                           dialog.dismiss()
                           val intent: Intent = Intent(context, MainActivity::class.java)
                           intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                           intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                           intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                           context?.startActivity(intent)
                           unwrap(context)?.finish()
                       }

                   }
               }.create()
               builder.show()*/
    }
}

