package com.evw.aster
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
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
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class publicprofileactivity2 : AppCompatActivity() {
    lateinit var webView: WebView
    lateinit var textView: TextView
    lateinit var imageView: ImageView
    lateinit var nameprofile: TextView
    lateinit var bioprofile: TextView
    lateinit var textView2: TextView
    lateinit var progressBar: MaterialNeonProgressBar
    lateinit var chip: Chip
    var user:String? = null
    var url:String? = null
    var postername:String? = null
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    lateinit var relativeLayout: RelativeLayout
    var datasnap:String? = null
    var roname:String? = null
    var urlpart:String? = null
    var avaurl:String? = null
    var fullname:String? = null
    var myname:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publicprofileactivity2)
        textView = findViewById(R.id.username_profile3)
        textView2 = findViewById(R.id.no_avatra2)
        imageView = findViewById(R.id.profile_view3)
        nameprofile = findViewById(R.id.name_profile3)
        bioprofile = findViewById(R.id.bio_profile3)
        progressBar = findViewById(R.id.profile_bar3)
        relativeLayout = findViewById(R.id.pav)
        chip = findViewById(R.id.sendmessage1)
        webView = findViewById(R.id.webview3)
        val pc =  intent.getStringExtra("username")
        val nc = intent.getStringExtra("name")
        val oc = intent.getStringExtra("uid")
        textView.setText(pc)
        nameprofile.setText(nc)
        lifecycleScope.launch(Dispatchers.IO) {
            val sbg = FirebaseFirestore.getInstance().collection("Users").document(oc.toString()).get().await()
            val getsnap = sbg.get("profilepic")
            datasnap = getsnap.toString()
            val getbio = sbg.get("bio")
            val geturl = sbg.get("avatarurl")
            val getname = sbg.get("name")
            url = geturl.toString()
            fullname = getname.toString()
            withContext(Dispatchers.Main) {
                if (geturl == null) {
                    progressBar.visibility = View.GONE
                    textView2.visibility = View.VISIBLE
                    user = ""
                    postername = ""
                } else {
                    postername = "file:///android_asset/poster.svg"
                    user = url!!.replace(".glb",".glb?quality=medium")
                }


                if (getbio == null){
                    bioprofile.text = "Aster User"
                } else if(getbio == ""){
                    bioprofile.text = "Aster User"
                } else {
                    bioprofile.text = getbio.toString()
                }

                if (getsnap == null) {
                    imageView.setImageResource(R.drawable.blankprofile)
                } else {
                    Picasso.get().load(getsnap.toString()).placeholder(R.drawable.blankprofile).into(imageView)
                }

            }

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






        chip.setOnClickListener {
            FirebaseFirestore.getInstance().collection("FriendsList").document(oc.toString()).collection("friends").document(uid.toString()).get().addOnCompleteListener { firstsnapshot ->
                FirebaseFirestore.getInstance().collection("FriendsList").document(uid.toString()).collection("friends").document(oc.toString()).get().addOnCompleteListener { Secondsnapshot ->
                    FirebaseFirestore.getInstance().collection("Blockaccounts").document(uid.toString()).collection("accounts").document(oc.toString()).get().addOnCompleteListener { fourthsnapshot ->
                        if (firstsnapshot.result.exists() && Secondsnapshot.result.exists()){
                            if (fourthsnapshot.result.exists()){
                                Toast.makeText(this,"First unblock this account",Toast.LENGTH_LONG).show()
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

                            }

                        } else {
                            Toast.makeText(this,"you are not friends yet",Toast.LENGTH_LONG).show()
                        }
                    }











                }

            }






        }


        relativeLayout.setOnClickListener {
            showmedialog(oc,datasnap,pc)

        }

    }

    private fun showmedialog(oc: String?, nc: String?, pc: String?) {
        val dialog : Dialog = Dialog(this)
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
                        Toast.makeText(this, "Blocked", Toast.LENGTH_LONG).show()
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
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)


    }


}