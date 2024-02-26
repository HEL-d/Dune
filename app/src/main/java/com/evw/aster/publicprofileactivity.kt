package com.evw.aster

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class publicprofileactivity : AppCompatActivity() {
    lateinit var webView: WebView
    lateinit var textView: TextView
    lateinit var imageView: ImageView
    lateinit var nameprofile:TextView
    lateinit var bioprofile:TextView
    lateinit var textView2: TextView
    lateinit var progressBar: MaterialNeonProgressBar
    lateinit var chip1:Chip
    lateinit var chip2: Chip
    var user:String? = null
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    var notificationsnapshot:String? = null
    var username:String? = null
    var name:String? = null
    var postername:String? = null
 lateinit var progressdialog:Dialog
 lateinit var cyanborderdialog: Cyanborderdialog
  lateinit var relativeLayout: RelativeLayout
   var datasnap:String? = null
    var getmedata:Any? = null
    var roname:String? = null
    var fullname:String? = null
    var urlpart:String? = null
    var avaurl:String? = null
    var myname:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publicprofileactivity)
        textView = findViewById(R.id.username_profile2)
        textView2 = findViewById(R.id.no_avatra2)
        imageView = findViewById(R.id.profile_view)
        nameprofile = findViewById(R.id.name_profile)
        bioprofile = findViewById(R.id.bio_profile)
        progressBar = findViewById(R.id.profile_bar2)
        chip1 = findViewById(R.id.sendreuest1)
        chip2 = findViewById(R.id.sendmessage2)
        webView = findViewById(R.id.webview2)
        relativeLayout = findViewById(R.id.pav)
        progressdialog = Dialog(this)
        initDialog()
        cyanborderdialog = Cyanborderdialog()
        val pc =  intent.getStringExtra("username")
        val nc = intent.getStringExtra("profilepic")
        val oc = intent.getStringExtra("uid")
        val pnc = intent.getStringExtra("avatarurl")
        textView.setText(pc)
        if (nc == null){
            imageView.setImageResource(R.drawable.blankprofile)
        } else {
            Picasso.get().load(nc).placeholder(R.drawable.blankprofile).into(imageView)
        }
        if (pnc == "null"){
            postername = ""
            textView2.visibility = View.VISIBLE
            user = ""
        } else {
            postername = "file:///android_asset/poster.svg"
            user = pnc?.replace(".glb",".glb?quality=medium")

        }
        webView.loadUrl("file:///android_asset/Modelview.html")
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = true
        }
        webView.webViewClient = object: WebViewClient(){
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                webView.evaluateJavascript("javascript: " +"updateFrompos(\"" + postername + "\")",null)
                webView.evaluateJavascript("javascript: " +"updateFromNative(\"" + user + "\")",null)
                progressBar.visibility = View.GONE
            }
        }

        lifecycleScope.launch(Dispatchers.IO){
            val sbg = FirebaseFirestore.getInstance().collection("Users").document(oc.toString()).get().await()
            val getsnap = sbg.get("name")
            val getbio = sbg.get("bio")
            val getname = sbg.get("name")
            fullname = getname.toString()



            withContext(Dispatchers.Main){
                if (getbio == null){
                    bioprofile.text = "Aster User"
                } else if(getbio == ""){
                    bioprofile.text = "Aster User"
                } else {
                    bioprofile.text = getbio.toString()
                }
                nameprofile.text = getsnap.toString()
            }

        }


        getchipsttus(oc)

        lifecycleScope.launch(Dispatchers.IO){
            getUsername()
        }

        FirebaseFirestore.getInstance().collection("FriendsList").document(uid.toString()).collection("friends").document(oc.toString()).get().addOnCompleteListener {
            roname = it.result.get("roomname").toString()
        }


        FirebaseFirestore.getInstance().collection("Users").document(uid.toString()).get().addOnCompleteListener {
            val tap = it.result.get("avatarurl")
            val bname = it.result.get("name")
            myname = bname.toString()
            if (tap != null){
                avaurl = tap.toString()
                val tpp = avaurl!!.substringAfter("https://models.readyplayer.me/")
                urlpart = tpp.replace(".glb","")
            }

        }




    chip1.setOnClickListener {
        if (chip1.text == "Send Request"){
            chip1.setChipBackgroundColorResource(R.color.Transparent)
            chip1.chipStrokeWidth = 2F
            chip1.setText("Request Sent")
            chip1.setTextAppearance(R.style.ChipTextStyle_Selected)
            lifecycleScope.launch(Dispatchers.IO){
                 sendfriendrequest(oc)
             }
        } else if (chip1.text == "Request Sent"){
            FirebaseFirestore.getInstance().collection("Notifications").document(oc.toString()).collection("notify").document(uid.toString()).delete().addOnSuccessListener {
                chip1.setChipBackgroundColorResource(R.color.Aster_neo)
                chip1.setText("Send Request")
            }
        } else if (chip1.text == "Accept request"){
               cyanborderdialog.makedialog(this,"Go to notification tab and accept the","request to start mich with this username")
        } else if (chip1.text === "Friend"){
            cyanborderdialog.makedialog(this,"you are already a friend, to remove this","user from friendslist go to friends tab")
           }



         //  sendfriendrequest(oc)


    }


   relativeLayout.setOnClickListener {
       showmedialog(oc,nc,pc)

   }

        chip2.setOnClickListener {
            FirebaseFirestore.getInstance().collection("FriendsList").document(oc.toString()).collection("friends").document(uid.toString()).get().addOnCompleteListener { firstsnapshot ->
                FirebaseFirestore.getInstance().collection("FriendsList").document(uid.toString()).collection("friends").document(oc.toString()).get().addOnCompleteListener { Secondsnapshot ->
                    FirebaseFirestore.getInstance().collection("Blockaccounts").document(uid.toString()).collection("accounts").document(oc.toString()).get().addOnCompleteListener { fourthsnapshot ->
                                if (firstsnapshot.result.exists() && Secondsnapshot.result.exists()) {
                                    if (fourthsnapshot.result.exists()) {
                                        Toast.makeText(this, "First unblock this account", Toast.LENGTH_LONG).show()
                                    } else {
                                        FirebaseFirestore.getInstance().collection("Users").document(uid.toString()).get().addOnCompleteListener {
                                            val ur = it.result.get("avatarurl")
                                            val mic = it.result.get("michHeight")
                                            val michvalue = mic.toString()
                                            if (ur != null){
                                                if (mic != null){
                                                    val pj : MutableMap<String, Any> = hashMapOf()
                                                    pj.put("roomname",roname.toString())
                                                    pj.put("vid",oc.toString())
                                                    pj.put("name",fullname.toString())
                                                    pj.put("uidd",uid.toString())
                                                    val nj : MutableMap<String, Any> = hashMapOf()
                                                    nj.put("roomname",roname.toString())
                                                    nj.put("vid",uid.toString())
                                                    nj.put("name",myname.toString())
                                                    nj.put("uidd",oc.toString())
                                                    FirebaseDatabase.getInstance().getReference("Usersrooms").child(uid.toString()).child(oc.toString())
                                                        .updateChildren(pj).addOnSuccessListener {
                                                            FirebaseDatabase.getInstance().getReference("Usersrooms").child(oc.toString()).child(uid.toString())
                                                                .updateChildren(nj).addOnSuccessListener{
                                                                    val intent = Intent(Intent(this,GameActivity::class.java))
                                                                    intent.putExtra("urlpart",urlpart)
                                                                    intent.putExtra("url",avaurl)
                                                                    intent.putExtra("roomname",roname)
                                                                    intent.putExtra("mich",michvalue)
                                                                    intent.putExtra("vid",oc)
                                                                    startActivity(intent)
                                                                }
                                                        }

                                                }else {
                                                    val pj : MutableMap<String, Any> = hashMapOf()
                                                    pj.put("roomname",roname.toString())
                                                    pj.put("vid",oc.toString())
                                                    pj.put("name",fullname.toString())
                                                    pj.put("uidd",uid.toString())
                                                    val nj : MutableMap<String, Any> = hashMapOf()
                                                    nj.put("roomname",roname.toString())
                                                    nj.put("vid",uid.toString())
                                                    nj.put("name",myname.toString())
                                                    nj.put("uidd",oc.toString())
                                                    FirebaseDatabase.getInstance().getReference("Usersrooms").child(uid.toString()).child(oc.toString())
                                                        .updateChildren(pj).addOnSuccessListener {
                                                            FirebaseDatabase.getInstance().getReference("Usersrooms").child(oc.toString()).child(uid.toString())
                                                                .updateChildren(nj)  .addOnSuccessListener {
                                                                    val intent = Intent(Intent(this,GameActivity::class.java))
                                                                    intent.putExtra("urlpart",urlpart)
                                                                    intent.putExtra("url",avaurl)
                                                                    intent.putExtra("roomname",roname)
                                                                    intent.putExtra("mich","null")
                                                                    intent.putExtra("vid",oc)
                                                                    startActivity(intent)
                                                                }



                                                        }

                                                }

                                            } else {
                                                Toast.makeText(this,"you don't have avatar yet",Toast.LENGTH_LONG).show()
                                            }

                                        }
















                                       /* FirebaseDatabase.getInstance().getReference("Usersrooms").child(uid.toString()).child(oc.toString()).get().addOnCompleteListener {
                                                val data = it.result.child("block")
                                                if (data.exists()) {
                                                    Toast.makeText(this, "take value of roomid and go to chat", Toast.LENGTH_LONG).show()
                                                } else {
                                                    Toast.makeText(this, "create all necessary info here and then go to chat", Toast.LENGTH_LONG).show()
                                                }
                                            }
*/
                                    }

                                } else {
                                    Toast.makeText(this, "you are not friends yet", Toast.LENGTH_LONG).show()
                                }
                            }


                    }
            }






        }


















    }

    private fun showmedialog(oc: String?, nc: String?, pc: String?) {
        val dialog :Dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.sheetdialog)
        val block :TextView = dialog.findViewById(R.id.block)
        val report:TextView = dialog.findViewById(R.id.report)
        val gg = FirebaseFirestore.getInstance().collection("Blockaccounts").document(uid.toString()).collection("accounts").document(oc.toString()).addSnapshotListener { value, error ->
            if (value != null) {
                if (value.exists()){
                   block.setText("Unblock Account")
                } else {
                    block.setText("Block Account")
                }


            }
        }

        block.setOnClickListener {
            if (block.text == "Block Account") {
                val map: HashMap<String, Any> = hashMapOf()
                map.put("block",true)
                val data = hashMapOf("block" to true, "timestamp" to FieldValue.serverTimestamp(), "profilepic" to nc.toString(),"Username" to pc)
                FirebaseFirestore.getInstance().collection("Blockaccounts").document(uid.toString())
                    .collection("accounts").document(oc.toString()).set(data)
                    .addOnSuccessListener {
                        Toast.makeText(this,"Blocked",Toast.LENGTH_LONG).show()
                        dialog.dismiss()

                    }
            } else {
                val map: HashMap<String, Any> = hashMapOf()
                map.put("block",false)
                FirebaseFirestore.getInstance().collection("Blockaccounts").document(uid.toString())
                    .collection("accounts").document(oc.toString()).delete().addOnSuccessListener {
                        Toast.makeText(this,"Unblocked",Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                    }
            }



        }

        report.setOnClickListener {
            val map: HashMap<String, Any> = hashMapOf()
            map.put("account",oc.toString())
            FirebaseFirestore.getInstance().collection("ReportedAccount").document(uid.toString())
                .collection("accounts").document(oc.toString()).set(map).addOnSuccessListener {
                    Toast.makeText(this,"Reported Suceesfully",Toast.LENGTH_LONG).show()
                    dialog.dismiss()
                }
        }
        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)

    }


    private fun initDialog() {
        progressdialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressdialog.setCancelable(false)
        progressdialog.setContentView(R.layout.progressdialog)
    }












    private  fun getchipsttus(oc: String?) {

        FirebaseFirestore.getInstance().collection("FriendsList").document(oc.toString()).collection("friends").document(uid.toString()).get().addOnCompleteListener { firstsnapshot ->
            FirebaseFirestore.getInstance().collection("FriendsList").document(uid.toString()).collection("friends").document(oc.toString()).get().addOnCompleteListener { secondsnapshot ->
                  if (secondsnapshot.result.exists()){
                      chip1.setChipBackgroundColorResource(R.color.Transparent)
                      chip1.chipStrokeWidth = 2F
                      chip1.setText("Friend")
                      chip1.setTextAppearance(R.style.ChipTextStyle_Selected)
                  } else {
                   FirebaseFirestore.getInstance().collection("Notifications").document(oc.toString()).collection("notify").document(uid.toString()).get().addOnCompleteListener { thirdsnapshot ->
                          FirebaseFirestore.getInstance().collection("Notifications").document(uid.toString()).collection("notify").document(oc.toString()).get().addOnCompleteListener { fourthsnapshot->
                              if (fourthsnapshot.result.exists() && !thirdsnapshot.result.exists()){
                                  val getdata = fourthsnapshot.result.get("lptext")
                                  if (getdata == ""){
                                      chip1.setChipBackgroundColorResource(R.color.Transparent)
                                      chip1.chipStrokeWidth = 2F
                                      chip1.setText("Accept request")
                                      chip1.setTextAppearance(R.style.ChipTextStyle_Selected)
                                  }

                              } else if (thirdsnapshot.result.exists() && !fourthsnapshot.result.exists()){
                                  val getdata = thirdsnapshot.result.get("lptext")
                                   if (getdata == ""){
                                       chip1.setChipBackgroundColorResource(R.color.Transparent)
                                       chip1.chipStrokeWidth = 2F
                                       chip1.setText("Request Sent")
                                       chip1.setTextAppearance(R.style.ChipTextStyle_Selected)
                                   }
                              } else if(thirdsnapshot.result.exists() && fourthsnapshot.result.exists()){
                                  //no result
                              }

                          }
                      }

                  }
            }



        }


    }






    private suspend fun sendfriendrequest(oc: String?): Boolean {
        return try {
            if (oc == uid){
                withContext(Dispatchers.Main){
                    Toast.makeText(this@publicprofileactivity,"same uid", Toast.LENGTH_SHORT).show()
                }
            } else {
                val data = hashMapOf("username" to username, "uid" to uid, "lptext" to "","name" to name,"requestText"
                        to "sent you request","timestamp" to FieldValue.serverTimestamp(),"isread" to "false","frid" to oc.toString())
                val db = FirebaseFirestore.getInstance().collection("Notifications").document(oc.toString()).collection("notify")
                    .document(uid.toString()).set(data).await()
            }
            true

        } catch (e:Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@publicprofileactivity,"Failed to send", Toast.LENGTH_SHORT).show()
            }
            false
        }

    }


    private suspend fun getUsername() :DocumentSnapshot? {
        return try {
            val doc = FirebaseFirestore.getInstance().collection("Users").document(uid.toString()).get().await()
            val dbusername = doc.get("username")
            val dbname = doc.get("name")
            username = dbusername.toString()
            name = dbname.toString()
            doc
        } catch (e:Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@publicprofileactivity, "Nothing found", Toast.LENGTH_SHORT).show()
            }
            null
        }

    }







}