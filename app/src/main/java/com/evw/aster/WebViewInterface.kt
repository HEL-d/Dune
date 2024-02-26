package com.evw.aster
import android.content.Context
import android.webkit.JavascriptInterface
import android.webkit.WebMessage
import com.google.gson.Gson


class WebViewInterface(private val context: Context,private val callback: (WebMessage) -> Unit) {

    private var isCallbackAdded = false
    data class WebMessage(
        val type: String = "",
        val source: String = "readyplayerme",
        val eventName: String = "event",
        val data: Map<String, String>
    )

    object WebViewEvents {
        const val AVATAR_EXPORT = "v1.avatar.exported"
        const val USER_SET = "v1.user.set"
        const val USER_UPDATED = "v1.user.updated"
        const val USER_AUTHORIZED = "v1.user.authorized"
        const val ASSET_UNLOCK = "v1.asset.unlock"
        const val USER_LOGOUT = "v1.user.logout"
    }

    data class AssetRecord(
        val userId: String,
        val assetId: String
    )



    @JavascriptInterface
    fun receiveData(json: String){
        // extract avatar url from received message


        val gson = Gson()
        val webMessage = gson.fromJson(json, WebMessage::class.java)
        callback(webMessage)








     /*   val url = if(text.endsWith(".glb")) text else JSONObject(text).getJSONObject("data").getString("url");
        val profilepic:String = url.replace(".glb",".png?size=230")
        // copy to clipboard
        val data = ClipData.newPlainText("Ready Player Me", url)
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.setPrimaryClip(data)
        Toast.makeText(context, "Url copied into clipboard.", Toast.LENGTH_SHORT).show()

        // display modal window with the avatar url
         if (avatarbool == "true"){
             val updateavatardilog = Updateavatardilog()
             updateavatardilog.takedilaog(context, url,profilepic,phoneNumber,username)
         } else if (avatarbool == "false"){
             val welcomedialog = Welcomedialog()
             welcomedialog.givedilaog(context, url,profilepic,phoneNumber,username)
         }*/





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

