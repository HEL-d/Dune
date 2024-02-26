package com.evw.aster

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.plusAssign


import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.unity3d.player.UnityPlayer
import com.unity3d.player.UnityPlayerActivity
import org.json.JSONObject


class GameActivity:UnityPlayerActivity() {

      companion object{
          private const val TAG = "GameActivity"
      }


    lateinit var textView: TextView
    lateinit var relativeLayout2: RelativeLayout
    lateinit var button: RelativeLayout
    lateinit var linerlayout: LinearLayout
    lateinit var constraintLayout: ConstraintLayout
    var xdown = 0f
    var ydown:Float = 0f
    var givemedistance:Int = 0
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    var count:Int = 0
    var reciverRoom:String? = null
    var senderRoom:String? = null
    lateinit var mlist:ArrayList<MessageClass>
    lateinit var ulist:ArrayList<MessageClass>
    lateinit var uplist:ArrayList<MessageClass>
    lateinit var chatAdapter: ChatAdapter
    lateinit var recyclerView: RecyclerView
    lateinit var recylerview2: RecyclerView
    lateinit var editText: EditText
    lateinit var imageButton1: ImageButton
    lateinit var imageButton2: ImageButton
    lateinit var BAdapter:BAdapter
    var valuenow:String? = null
    lateinit var popupWindow: PopupWindow
    var animy:String = "none"
    var type:Int = 0
    var active = false
    var inkid:String? = null
    var urlpart:String? = null
    lateinit var relativeLayout12: RelativeLayout
    var ctimer : CountDownTimer? = null
    lateinit var img: ImageView
    lateinit var shine: ImageView
    lateinit var fingerimage:ImageView
    var reciverUrl:String = ""
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

        val rm = intent.getStringExtra("roomname")
        val pm = intent.getStringExtra("url")
       val mich = intent.getStringExtra("mich")
      UnityPlayer.UnitySendMessage("CreateRoomMenu", "getroomname", rm)
        UnityPlayer.UnitySendMessage("CreateRoomMenu", "getavatarurl", pm)
      ulist = arrayListOf()
        mlist = arrayListOf()
        uplist = arrayListOf()
        val reciveruid = intent.getStringExtra("vid")
        inkid = reciveruid
        senderRoom = reciveruid + uid
        reciverRoom = uid + reciveruid
        urlpart = intent.getStringExtra("urlpart")
         val builder = AlertDialog.Builder(this)
          builder.setCancelable(false)
        builder.setPositiveButton("OK",
            DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                UnityPlayer.UnitySendMessage("RoomActionCanvas","OnClick_Leave","")
                Handler(Looper.getMainLooper()).postDelayed({
                    mUnityPlayer.destroy()
                },500)
            })
        ctimer = object : CountDownTimer(8000, 1000) {
            override fun onTick(millisUntilFinished: Long) {

                // logic to set the EditText could go here

            }

            override fun onFinish() {
                UnityPlayer.UnitySendMessage(urlpart,valuenow,"false")

            }
        }




        FirebaseFirestore.getInstance().collection("Blockaccounts").document(reciveruid.toString()).collection("accounts").document(uid.toString()).get().addOnCompleteListener { firstsnap ->
            if (firstsnap.result.exists()){

                 builder.setMessage("this action can't be performed")
                val alertDialog = builder.create()
                 alertDialog.show()


            } else {
                FirebaseFirestore.getInstance().collection("Blockaccounts").document(uid.toString()).collection("accounts").document(reciveruid.toString()).get().addOnCompleteListener { secondsnp->
                    if (secondsnp.result.exists()){
                      builder.setMessage("First unblock this account")
                        val alertDialog = builder.create()
                        alertDialog.show()
                    }

                }


            }

        }




        FirebaseFirestore.getInstance().collection("Users").document(reciveruid.toString()).get().addOnCompleteListener {
             val uri = it.result.get("avatarurl")
             if (uri != null){
                val myurl = uri.toString()
                 val tpp = myurl.substringAfter("https://models.readyplayer.me/")
                 reciverUrl = tpp.replace(".glb","")
             }


         }


        mUnityPlayer += Yeview(this).apply {
           layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT).apply {
               constraintLayout = findViewById(R.id.briclayout)
               textView = findViewById(R.id.cvv)
               editText = findViewById<EditText>(R.id.edittextm)
               imageButton1 = findViewById(R.id.sendbutton)
                imageButton2 = findViewById(R.id.emo)
               img = findViewById(R.id.img)
               shine = findViewById(R.id.shine)
               fingerimage = findViewById(R.id.fringer)
                relativeLayout12 = findViewById(R.id.mainrelative)
               relativeLayout2 = findViewById(R.id.button28)
               button = findViewById(R.id.savebutton)
                linerlayout = findViewById(R.id.ncc)
               recyclerView = findViewById<RecyclerView>(R.id.Err)
               recylerview2 = findViewById<RecyclerView>(R.id.prr)
                val lm : LinearLayoutManager = LinearLayoutManager(context,RecyclerView.VERTICAL,true)
                lm.stackFromEnd = true
                recylerview2.layoutManager = lm
                val opm :LinearLayoutManager = LinearLayoutManager(context,RecyclerView.VERTICAL,true)
               opm.stackFromEnd = true
                recyclerView.layoutManager = opm
               chatAdapter = ChatAdapter(context)
                BAdapter = BAdapter(context)
                recyclerView.adapter = BAdapter
               recylerview2.adapter = chatAdapter





               imageButton1.setOnClickListener {
                   if (editText.text.isNotEmpty()){
                      if(animy == "none"){
                            sendData(editText.text.toString(),reciveruid.toString(),"none")
                        } else if (animy == "smile") {
                           sendData(editText.text.toString(),reciveruid.toString(),"smile")
                        } else if (animy == "happy"){
                            sendData(editText.text.toString(),reciveruid.toString(),"happy")
                        } else if (animy == "kiss"){
                           sendData(editText.text.toString(),reciveruid.toString(),"kiss")
                       } else if (animy == "angry"){
                            sendData(editText.text.toString(),reciveruid.toString(),"angry")
                        } else if (animy == "cry"){
                           sendData(editText.text.toString(),reciveruid.toString(),"cry")
                        } else if (animy == "clap"){
                           sendData(editText.text.toString(),reciveruid.toString(),"clap")
                       } else if (animy == "amaze"){
                           sendData(editText.text.toString(),reciveruid.toString(),"amaze")
                        } else if (animy == "headnod"){
                           sendData(editText.text.toString(),reciveruid.toString(),"headnod")
                        } else if (animy == "headingno"){
                           sendData(editText.text.toString(),reciveruid.toString(),"headingno")
                       } else if (animy == "handshake"){
                            sendData(editText.text.toString(),reciveruid.toString(),"handshake")
                       } else if (animy == "dance"){
                            sendData(editText.text.toString(),reciveruid.toString(),"dance")
                        } else if (animy == "celebration"){
                           sendData(editText.text.toString(),reciveruid.toString(),"celebration")
                       } else if (animy == "sad"){
                           sendData(editText.text.toString(),reciveruid.toString(),"sad")
                       } else if (animy == "victory"){
                           sendData(editText.text.toString(),reciveruid.toString(),"victory")
                       } else if (animy == "insult"){
                            sendData(editText.text.toString(),reciveruid.toString(),"insult")
                        } else if (animy == "pray"){
                           sendData(editText.text.toString(),reciveruid.toString(),"pray")
                      } else if (animy == "salute"){
                           sendData(editText.text.toString(),reciveruid.toString(),"salute")
                       } else if (animy == "proud"){
                          sendData(editText.text.toString(),reciveruid.toString(),"proud")
                      } else if (animy == "blush"){
                          sendData(editText.text.toString(),reciveruid.toString(),"blush")
                      } else if (animy == "thumbup"){
                          sendData(editText.text.toString(),reciveruid.toString(),"thumbup")
                      } else if (animy == "thumbdown"){
                          sendData(editText.text.toString(),reciveruid.toString(),"thumbdown")

                      } else if(animy == "nointer"){
                          sendData(editText.text.toString(),reciveruid.toString(),"nointer")
                      } else if (animy == "thank"){
                          sendData(editText.text.toString(),reciveruid.toString(),"thank")
                      } else if (animy == "talk"){
                          sendData(editText.text.toString(),reciveruid.toString(),"talk")
                      } else if (animy == "cringe"){
                          sendData(editText.text.toString(),reciveruid.toString(),"cringe")
                      }

                    } else {
                       Toast.makeText(context,"Empty", Toast.LENGTH_SHORT).show()

                   }

               }


               constraintLayout.setOnTouchListener(View.OnTouchListener { v, event ->
                   count++
                   if (count == 2 && mich == "null") {
                       textView.visibility = View.GONE
                        Handler(Looper.getMainLooper()).postDelayed({
                           relativeLayout2.visibility = View.VISIBLE
                            linerlayout.visibility = View.VISIBLE
                            UnityPlayer.UnitySendMessage("Road","activatenow","")
                            UnityPlayer.UnitySendMessage("mask","maskme","")
                                                                    }, 4000)

                       Handler(Looper.getMainLooper()).postDelayed({
                              fingerimage.visibility = View.VISIBLE
                              val   mAnimation = TranslateAnimation(
                                  TranslateAnimation.ABSOLUTE, 0f,
                                  TranslateAnimation.ABSOLUTE, 0f,
                                  TranslateAnimation.RELATIVE_TO_PARENT, 0f,
                                  TranslateAnimation.RELATIVE_TO_PARENT, 0.35f
                              )
                              mAnimation.setDuration(3000)
                              mAnimation.setRepeatCount(-1)
                              mAnimation.setRepeatMode(Animation.RESTART)
                              mAnimation.setInterpolator(LinearInterpolator())
                              fingerimage.setAnimation(mAnimation)
                          },4000)
                   } else if (count == 2 && mich != "null"){
                        linerlayout.visibility = View.VISIBLE
                        linerlayout.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                       linerlayout.layoutParams.height = mich!!.toInt()
                        Handler(Looper.getMainLooper()).postDelayed({
                            getDatafromDatabase()
                            getDatareciverroom()
                            relativeLayout12.visibility = View.VISIBLE
                        },2000)
                       FirebaseFirestore.getInstance().collection("Triggers").document(uid.toString()).collection(reciveruid.toString()).addSnapshotListener(object:
                           EventListener<QuerySnapshot> {
                           override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                               if (value != null){
                                   for (document in value.documentChanges){
                                       if (document.type == DocumentChange.Type.ADDED){
                                           Handler(Looper.getMainLooper()).postDelayed({
                                               UnityPlayer.UnitySendMessage("Camera","searchmyavatar",urlpart + '_'+ reciverUrl)
                                           },5000)
                                       } else if (document.type == DocumentChange.Type.MODIFIED){
                                           Handler(Looper.getMainLooper()).postDelayed({
                                               UnityPlayer.UnitySendMessage("Camera","searchmyavatar",urlpart + '_'+ reciverUrl)
                                           },5000)
                                       }

                                   }
                               }
                           }

                       })

                       Handler(Looper.getMainLooper()).postDelayed({
                           val city = hashMapOf("trigger" to FieldValue.serverTimestamp())
                           FirebaseFirestore.getInstance().collection("Triggers").document(reciveruid.toString()).collection(uid.toString()).document("data").set(city).addOnSuccessListener{
                               FirebaseFirestore.getInstance().collection("Triggers").document(uid.toString()).collection(reciveruid.toString()).document("data").set(city)
                           }
                       },10000)












                    }


                    false
                })




                relativeLayout2.setOnTouchListener(View.OnTouchListener({ v, event ->
                    fingerimage.clearAnimation()
                    fingerimage.visibility = View.GONE
                    button.visibility = View.VISIBLE
                    ShineAnimation()
                    val x = event.x.toInt()
                    val y = event.y.toInt()
                    val width = linerlayout.layoutParams.width
                    val height = linerlayout.layoutParams.height
                    when (event.actionMasked) {
                       MotionEvent.ACTION_DOWN -> {
                          xdown = event.x
                           ydown = event.y
                       }
                        MotionEvent.ACTION_MOVE -> {
                            val movedX: Float
                            val movedY: Float
                            movedX = event.x
                            movedY = event.y
                            val distanceY: Float  = movedY - ydown
                            linerlayout.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                            linerlayout.layoutParams.height = (v.y + distanceY).toInt()  + v.height
                            linerlayout.requestLayout()
                            //    val distanceX: Float = movedX - xdown
                            //   v.setX(v.getX() + distanceX)
                            v.y = v.y + distanceY
                            givemedistance = (v.y.toInt() + distanceY).toInt() + v.height
                        }


                    }




                    true
                }))

                button.setOnClickListener {
                    FirebaseFirestore.getInstance().collection("Users").document(uid.toString()).update("michHeight",givemedistance.toString()).addOnSuccessListener {
                        Toast.makeText(context,"Successfully Updated", Toast.LENGTH_LONG).show()
                           textView.visibility = View.VISIBLE
                           relativeLayout2.visibility = View.GONE
                           Handler(Looper.getMainLooper()).postDelayed({
                               button.visibility = View.GONE
                           },2000)
                       linerlayout.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                        linerlayout.layoutParams.height = givemedistance
                        relativeLayout12.visibility = View.VISIBLE
                        UnityPlayer.UnitySendMessage("Road","disablenow","")
                        UnityPlayer.UnitySendMessage("mask","unmaskme","")
                        getDatafromDatabase()
                        getDatareciverroom()
                        FirebaseFirestore.getInstance().collection("Triggers").document(reciveruid.toString()).collection(uid.toString()).addSnapshotListener(object:
                            EventListener<QuerySnapshot> {
                            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                                if (value != null){
                                    for (document in value.documentChanges){
                                        if (document.type == DocumentChange.Type.ADDED){
                                            Handler(Looper.getMainLooper()).postDelayed({
                                                UnityPlayer.UnitySendMessage("Camera","searchmyavatar",urlpart + '_'+ reciverUrl)
                                            },5000)
                                        } else if (document.type == DocumentChange.Type.MODIFIED){
                                            Handler(Looper.getMainLooper()).postDelayed({
                                                UnityPlayer.UnitySendMessage("Camera","searchmyavatar",urlpart + '_'+ reciverUrl)
                                            },5000)
                                        }

                                    }
                                }
                            }

                        })

                        Handler(Looper.getMainLooper()).postDelayed({
                            val city = hashMapOf("trigger" to FieldValue.serverTimestamp())
                            FirebaseFirestore.getInstance().collection("Triggers").document(reciveruid.toString()).collection(uid.toString()).document("data").set(city).addOnSuccessListener {
                                FirebaseFirestore.getInstance().collection("Triggers").document(uid.toString()).collection(reciveruid.toString()).document("data").set(city)
                            }
                        },10000)





                    }


              }

               textView.setOnClickListener {
                  UnityPlayer.UnitySendMessage("RoomActionCanvas","OnClick_Leave","")
                       Handler(Looper.getMainLooper()).postDelayed({
                           mUnityPlayer.destroy()
                       },500)
              }

               imageButton2.setOnClickListener {
                    popupWindow.showAtLocation(editText,
                        Gravity.BOTTOM or Gravity.END,
                        0  ,
                        0
                   )
                }





                popupWindow =    PopupWindow(editText.context).apply {
                   isOutsideTouchable = true
                    val inflater = LayoutInflater.from(editText.context)
                    contentView = inflater.inflate(R.layout.emojiview, null).apply {
                      measure(
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                           View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
                           val textView1:TextView = this.findViewById(R.id.smile1)
                        val textView2:TextView = this.findViewById(R.id.smile2)
                        val textView3:TextView = this.findViewById(R.id.smile3)
                        val textView4:TextView = this.findViewById(R.id.smile4)
                        val textView5:TextView = this.findViewById(R.id.smile5)
                        val textView6:TextView = this.findViewById(R.id.smile6)
                        val textView7:TextView = this.findViewById(R.id.smile7)
                        val textView8:TextView = this.findViewById(R.id.smile8)
                        val textView9:TextView = this.findViewById(R.id.smile9)
                        val textView10:TextView = this.findViewById(R.id.smile10)
                        val textView11:TextView = this.findViewById(R.id.smile11)
                        val happy1:TextView = this.findViewById(R.id.happy1)
                        val happy2:TextView = this.findViewById(R.id.happy2)
                        val happy3:TextView = this.findViewById(R.id.happy3)
                        val happy4:TextView = this.findViewById(R.id.happy4)
                        val happy5:TextView = this.findViewById(R.id.happy5)
                        val happy6:TextView = this.findViewById(R.id.happy6)
                        val kiss1:TextView = this.findViewById(R.id.kiss1)
                        val kiss2:TextView = this.findViewById(R.id.kiss2)
                        val kiss3:TextView = this.findViewById(R.id.kiss3)
                        val kiss4:TextView = this.findViewById(R.id.kiss4)
                        val sad1:TextView = this.findViewById(R.id.sad1)
                        val sad2:TextView = this.findViewById(R.id.sad2)
                        val sad3:TextView = this.findViewById(R.id.sad3)
                        val sad4:TextView = this.findViewById(R.id.sad4)
                        val sad5:TextView = this.findViewById(R.id.sad5)
                        val sad6:TextView = this.findViewById(R.id.sad6)
                        val sad7:TextView = this.findViewById(R.id.sad7)
                        val sad8:TextView = this.findViewById(R.id.sad8)
                        val sad9:TextView = this.findViewById(R.id.sad9)
                        val sad10:TextView = this.findViewById(R.id.sad10)
                        val cry1:TextView = this.findViewById(R.id.cry1)
                        val cry2:TextView = this.findViewById(R.id.cry2)
                        val cry3:TextView = this.findViewById(R.id.cry3)
                        val cry4:TextView = this.findViewById(R.id.cry4)
                        val cry5:TextView = this.findViewById(R.id.cry5)
                        val angry1:TextView = this.findViewById(R.id.angry1)
                        val angry2:TextView = this.findViewById(R.id.angry2)
                        val angry3:TextView = this.findViewById(R.id.angry3)
                        val angry4:TextView = this.findViewById(R.id.angry4)
                        val amaze1:TextView = this.findViewById(R.id.amaze1)
                        val amaze2:TextView = this.findViewById(R.id.amaze2)
                        val amaze3:TextView = this.findViewById(R.id.amaze3)
                        val amaze4:TextView = this.findViewById(R.id.amaze4)
                        val amaze5:TextView = this.findViewById(R.id.amaze5)
                        val amaze6:TextView = this.findViewById(R.id.amaze6)
                        val amaze7:TextView = this.findViewById(R.id.amaze7)
                        val amaze8:TextView = this.findViewById(R.id.amaze8)
                        val amaze9:TextView = this.findViewById(R.id.amaze9)
                        val proud1:TextView = this.findViewById(R.id.proud1)
                        val blush1:TextView = this.findViewById(R.id.blush1)
                        val nointer1:TextView = this.findViewById(R.id.nointer1)
                        val nointer2:TextView = this.findViewById(R.id.nointer2)
                        val nointer3:TextView = this.findViewById(R.id.nointer3)
                        val thank1:TextView = this.findViewById(R.id.thank1)
                        val cringe1 :TextView = this.findViewById(R.id.cringe1)
                        val cringe2 :TextView = this.findViewById(R.id.cringe2)
                        val cringe3 : TextView = this.findViewById(R.id.cringe3)
                        val yawn1:TextView = this.findViewById(R.id.yawn1)
                        val yawn2:TextView = this.findViewById(R.id.yawn2)
                        val yawn3:TextView = this.findViewById(R.id.yawn3)
                        val celeb1:TextView = this.findViewById(R.id.celeb1)
                        val celeb2:TextView = this.findViewById(R.id.celeb2)
                        val devilangry1:TextView = this.findViewById(R.id.devilangry1)
                        val danav1:TextView = this.findViewById(R.id.danav1)
                        val fire:TextView = this.findViewById(R.id.fire)
                        val hundred:TextView = this.findViewById(R.id.hundred)
                        val sleepy :TextView = this.findViewById(R.id.sleepy)
                        val celeb3:TextView = this.findViewById(R.id.celeb3)
                        val monkeyblush1:TextView = this.findViewById(R.id.monkeyblush1)
                        val monkeyblush2:TextView = this.findViewById(R.id.monkeyblush2)
                        val catlaughing1:TextView = this.findViewById(R.id.catlaughing1)
                        val catlaughing2:TextView = this.findViewById(R.id.catlaughing2)
                        val catlaughing3:TextView = this.findViewById(R.id.catlaughing3)
                        val catlove1:TextView = this.findViewById(R.id.catlove1)
                        val catnointer:TextView = this.findViewById(R.id.catnointer)
                        val catkiss1:TextView = this.findViewById(R.id.catkiss1)
                        val catshock1:TextView = this.findViewById(R.id.catshock1)
                        val catcry:TextView = this.findViewById(R.id.catcry)
                        val catangry:TextView = this.findViewById(R.id.catangry)
                        val heart1:TextView = this.findViewById(R.id.heart1)
                        val heart2:TextView = this.findViewById(R.id.heart2)
                        val heart3:TextView = this.findViewById(R.id.heart3)
                        val heart4:TextView = this.findViewById(R.id.heart4)
                        val heart5:TextView = this.findViewById(R.id.heart5)
                        val heart6:TextView = this.findViewById(R.id.heart6)
                        val heart7:TextView = this.findViewById(R.id.heart7)
                        val heart8:TextView = this.findViewById(R.id.heart8)
                        val heartbreak1:TextView = this.findViewById(R.id.heartbreak1)
                        val lip1:TextView = this.findViewById(R.id.lip1)
                        val talk1:TextView = this.findViewById(R.id.talk1)
                        val eye1:TextView = this.findViewById(R.id.eye1)
                        val eye2:TextView = this.findViewById(R.id.eye2)
                        val strong1:TextView = this.findViewById(R.id.strong1)
                        val strong2:TextView = this.findViewById(R.id.strong2)
                        val strong3:TextView = this.findViewById(R.id.strong3)
                        val thumbup1:TextView = this.findViewById(R.id.thumbup1)
                        val thumbdown1 : TextView = this.findViewById(R.id.thumbdown)
                        val hanshake1 : TextView = this.findViewById(R.id.handshake1)
                        val yo1 :TextView = this.findViewById(R.id.yo1)
                        val yo2:TextView = this.findViewById(R.id.yo2)
                        val wave1:TextView = this.findViewById(R.id.wave1)
                        val wave2:TextView = this.findViewById(R.id.wave2)
                        val wave3:TextView = this.findViewById(R.id.wave3)
                         val pranam1:TextView = this.findViewById(R.id.pranam)
                         val insult1:TextView = this.findViewById(R.id.insult1)
                          val bowdown:TextView = this.findViewById(R.id.bowdon)
                           val girldance:TextView = this.findViewById(R.id.girldance)
                        val boydance:TextView = this.findViewById(R.id.boydance)
                        val right:TextView = this.findViewById(R.id.right1)
                        val wrong1:TextView = this.findViewById(R.id.wrong1)
                        val wrong2:TextView = this.findViewById(R.id.wrong2)
                        textView1.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if ((type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt())) {
                                    editText.append("\uD83D\uDE00")

                                } else {
                                    editText.append("\uD83D\uDE00")
                                    animy = "smile"

                                }
                            } else {
                                editText.append("\uD83D\uDE00")
                                animy = "smile"

                            }
                        }

                        textView2.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if ((type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt())) {
                                    editText.append("\uD83D\uDE04")

                                } else {
                                    editText.append("\uD83D\uDE04")
                                    animy = "smile"

                                }
                            } else {
                                editText.append("\uD83D\uDE04")
                                animy = "smile"

                            }
                        }

                        textView3.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if ((type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt())) {
                                    editText.append("\uD83D\uDE01")

                                } else {
                                    editText.append("\uD83D\uDE01")
                                    animy = "smile"

                                }
                            } else {
                                editText.append("\uD83D\uDE01")
                                animy = "smile"

                            }
                        }

                        textView4.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if ((type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt())) {
                                    editText.append("\uD83D\uDE03")

                                } else {
                                    editText.append("\uD83D\uDE03")
                                    animy = "smile"

                                }
                            } else {
                                editText.append("\uD83D\uDE03")
                                animy = "smile"

                            }
                        }

                        textView5.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if ((type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt())) {
                                    editText.append("\uD83D\uDE06")

                                } else {
                                    editText.append("\uD83D\uDE06")
                                    animy = "smile"

                                }
                            } else {
                                editText.append("\uD83D\uDE06")
                                animy = "smile"

                            }
                        }

                        textView6.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if ((type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt())) {
                                    editText.append("\uD83D\uDE05")

                                } else {
                                    editText.append("\uD83D\uDE05")
                                    animy = "smile"

                                }
                            } else {
                                editText.append("\uD83D\uDE05")
                                animy = "smile"

                            }
                        }

                        textView7.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if ((type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt())) {
                                    editText.append("\uD83E\uDD23")

                                } else {
                                    editText.append("\uD83E\uDD23")
                                    animy = "smile"

                                }
                            } else {
                                editText.append("\uD83E\uDD23")
                                animy = "smile"

                            }
                        }



                        textView8.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if ((type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt())) {
                                    editText.append("\uD83D\uDE02")

                                } else {
                                    editText.append("\uD83D\uDE02")
                                    animy = "smile"

                                }
                            } else {
                                editText.append("\uD83D\uDE02")
                                animy = "smile"

                            }
                        }

                        textView9.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if ((type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt())) {
                                    editText.append("\uD83D\uDE1D")

                                } else {
                                    editText.append("\uD83D\uDE1D")
                                    animy = "smile"

                                }
                            } else {
                                editText.append("\uD83D\uDE1D")
                                animy = "smile"

                            }
                        }


                        textView10.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if ((type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt())) {
                                    editText.append("\uD83D\uDE1C")

                                } else {
                                    editText.append("\uD83D\uDE1C")
                                    animy = "smile"

                                }
                            } else {
                                editText.append("\uD83D\uDE1C")
                                animy = "smile"

                            }
                        }



                        textView11.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if ((type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt())) {
                                    editText.append("\uD83D\uDE1B")

                                } else {
                                    editText.append("\uD83D\uDE1B")
                                    animy = "smile"

                                }
                            } else {
                                editText.append("\uD83D\uDE1B")
                                animy = "smile"

                            }
                        }

                        happy1.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE0D")
                                } else {
                                    editText.append("\uD83D\uDE0D")
                                    animy = "happy"
                                }
                            } else {
                                editText.append("\uD83D\uDE0D")
                                animy = "happy"
                            }
                        }

                        happy2.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83E\uDD70")
                                } else {
                                    editText.append("\uD83E\uDD70")
                                    animy = "happy"
                                }
                            } else {
                                editText.append("\uD83E\uDD70")
                                animy = "happy"
                            }

                        }

                        happy3.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE07")
                                } else {
                                    editText.append("\uD83D\uDE07")
                                    animy = "happy"
                                }
                            } else {
                                editText.append("\uD83D\uDE07")
                                animy = "happy"
                            }
                        }

                        happy4.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE0A")
                                } else {
                                    editText.append("\uD83D\uDE0A")
                                    animy = "happy"
                                }
                            } else {
                                editText.append("\uD83D\uDE0A")
                                animy = "happy"
                            }
                        }

                        happy5.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("☺\uFE0F")
                                } else {
                                    editText.append("☺\uFE0F")
                                    animy = "happy"
                                }
                            } else {
                                editText.append("☺\uFE0F")
                                animy = "happy"
                            }
                        }

                        happy6.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE0C")
                                } else {
                                    editText.append("\uD83D\uDE0C")
                                    animy = "happy"
                                }
                            } else {
                                editText.append("\uD83D\uDE0C")
                                animy = "happy"
                            }
                        }

                        kiss1.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()){
                                    editText.append("\uD83D\uDE17")
                                } else {
                                    editText.append("\uD83D\uDE17")
                                    animy = "kiss"
                                }
                            } else {
                                editText.append("\uD83D\uDE17")
                                animy = "kiss"
                            }

                        }


                        kiss2.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE1A")
                                } else {
                                    editText.append("\uD83D\uDE1A")
                                    animy = "kiss"
                                }
                            } else {
                                editText.append("\uD83D\uDE1A")
                                animy = "kiss"
                            }
                        }



                        kiss3.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE19")
                                } else {
                                    editText.append("\uD83D\uDE19")
                                    animy = "kiss"
                                }
                            } else {
                                editText.append("\uD83D\uDE19")
                                animy = "kiss"
                            }
                        }

                        kiss4.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE18")
                                } else {
                                    editText.append("\uD83D\uDE18")
                                    animy = "kiss"
                                }
                            } else {
                                editText.append("\uD83D\uDE18")
                                animy = "kiss"
                            }
                        }


                        sad1.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE1F")
                                } else {
                                    editText.append("\uD83D\uDE1F")
                                    animy = "sad"
                                }
                            } else {
                                editText.append("\uD83D\uDE1F")
                                animy = "sad"
                            }
                        }

                        sad2.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE41")
                                } else {
                                    editText.append("\uD83D\uDE41")
                                    animy = "sad"
                                }
                            } else {
                                editText.append("\uD83D\uDE41")
                                animy = "sad"
                            }
                        }


                        sad3.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("☹\uFE0F")
                                } else {
                                    editText.append("☹\uFE0F")
                                    animy = "sad"
                                }
                            } else {
                                editText.append("☹\uFE0F")
                                animy = "sad"
                            }
                        }

                        sad4.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE15")
                                } else {
                                    editText.append("\uD83D\uDE15")
                                    animy = "sad"
                                }
                            } else {
                                editText.append("\uD83D\uDE15")
                                animy = "sad"
                            }
                        }


                        sad5.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83E\uDD7A")
                                } else {
                                    editText.append("\uD83E\uDD7A")
                                    animy = "sad"
                                }
                            } else {
                                editText.append("\uD83E\uDD7A")
                                animy = "sad"
                            }
                        }


                        sad6.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()){
                                    editText.append("\uD83D\uDE16")
                                } else {
                                    editText.append("\uD83D\uDE16")
                                    animy = "sad"
                                }
                            } else {
                                editText.append("\uD83D\uDE16")
                                animy = "sad"
                            }

                        }

                        sad7.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()){
                                    editText.append("\uD83D\uDE23")
                                } else {
                                    editText.append("\uD83D\uDE23")
                                    animy = "sad"
                                }
                            } else {
                                editText.append("\uD83D\uDE23")
                                animy = "sad"
                            }

                        }

                        sad8.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()){
                                    editText.append("\uD83D\uDE1E")
                                } else {
                                    editText.append("\uD83D\uDE1E")
                                    animy = "sad"
                                }
                            } else {
                                editText.append("\uD83D\uDE1E")
                                animy = "sad"
                            }

                        }

                        sad9.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()){
                                    editText.append("\uD83D\uDE29")
                                } else {
                                    editText.append("\uD83D\uDE29")
                                    animy = "sad"
                                }
                            } else {
                                editText.append("\uD83D\uDE29")
                                animy = "sad"
                            }

                        }


                        sad10.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()){
                                    editText.append("\uD83D\uDE2B")
                                } else {
                                    editText.append("\uD83D\uDE2B")
                                    animy = "sad"
                                }
                            } else {
                                editText.append("\uD83D\uDE2B")
                                animy = "sad"
                            }

                        }


                        cry1.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE13")
                                } else {
                                    editText.append("\uD83D\uDE13")
                                    animy = "cry"
                                }
                            } else {
                                editText.append("\uD83D\uDE13")
                                animy = "cry"
                            }
                        }

                        cry2.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE30")
                                } else {
                                    editText.append("\uD83D\uDE30")
                                    animy = "cry"
                                }
                            } else {
                                editText.append("\uD83D\uDE30")
                                animy = "cry"
                            }
                        }



                        cry3.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE25")
                                } else {
                                    editText.append("\uD83D\uDE25")
                                    animy = "cry"
                                }
                            } else {
                                editText.append("\uD83D\uDE25")
                                animy = "cry"
                            }
                        }


                        cry4.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE22")
                                } else {
                                    editText.append("\uD83D\uDE22")
                                    animy = "cry"

                                }
                            } else {
                                editText.append("\uD83D\uDE22")
                                animy = "cry"
                            }
                        }


                        cry5.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE2D")
                                } else {
                                    editText.append("\uD83D\uDE2D")
                                    animy = "cry"

                                }
                            } else {
                                editText.append("\uD83D\uDE2D")
                                animy = "cry"
                            }
                        }


                        angry1.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE20")
                                } else {
                                    editText.append("\uD83D\uDE20")
                                    animy = "angry"
                                }
                            } else {
                                editText.append("\uD83D\uDE20")
                                animy = "angry"
                            }
                        }


                        angry2.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE24")
                                } else {
                                    editText.append("\uD83D\uDE24")
                                    animy = "angry"
                                }
                            } else {
                                editText.append("\uD83D\uDE24")
                                animy = "angry"
                            }
                        }

                        angry3.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE21")
                                } else {
                                    editText.append("\uD83D\uDE21")
                                    animy = "angry"
                                }
                            } else {
                                editText.append("\uD83D\uDE21")
                                animy = "angry"
                            }
                        }

                        angry4.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83E\uDD2C")
                                } else {
                                    editText.append("\uD83E\uDD2C")
                                    animy = "angry"
                                }
                            } else {
                                editText.append("\uD83E\uDD2C")
                                animy = "angry"
                            }
                        }











                        amaze1.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE2E")
                                } else {
                                    editText.append("\uD83D\uDE2E")
                                    animy = "amaze"
                                }
                            } else {
                                editText.append("\uD83D\uDE2E")
                                animy = "amaze"
                            }
                        }

                        amaze2.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE2F")
                                } else {
                                    editText.append("\uD83D\uDE2F")
                                    animy = "amaze"
                                }
                            } else {
                                editText.append("\uD83D\uDE2F")
                                animy = "amaze"
                            }
                        }

                        amaze3.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE32")
                                } else {
                                    editText.append("\uD83D\uDE32")
                                    animy = "amaze"
                                }
                            }else {
                                editText.append("\uD83D\uDE32")
                                animy = "amaze"
                            }
                        }


                        amaze4.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE27")
                                } else {
                                    editText.append("\uD83D\uDE27")
                                    animy = "amaze"
                                }
                            } else {
                                editText.append("\uD83D\uDE27")
                                animy = "amaze"
                            }
                        }

                        amaze5.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE28")
                                } else {
                                    editText.append("\uD83D\uDE28")
                                    animy = "amaze"
                                }
                            } else {
                                editText.append("\uD83D\uDE28")
                                animy = "amaze"
                            }
                        }

                        amaze6.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE26")
                                } else {
                                    editText.append("\uD83D\uDE26")
                                    animy = "amaze"
                                }
                            }else {
                                editText.append("\uD83D\uDE26")
                                animy = "amaze"
                            }
                        }

                        amaze7.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE33")
                                } else {
                                    editText.append("\uD83D\uDE33")
                                    animy = "amaze"
                                }
                            } else {
                                editText.append("\uD83D\uDE33")
                                animy = "amaze"
                            }
                        }

                        amaze8.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83E\uDD2F")
                                } else {
                                    editText.append("\uD83E\uDD2F")
                                    animy = "amaze"
                                }
                            } else {
                                editText.append("\uD83E\uDD2F")
                                animy = "amaze"
                            }
                        }

                        amaze9.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE31")
                                } else {
                                    editText.append("\uD83D\uDE31")
                                    animy = "amaze"
                                }
                            }else {
                                editText.append("\uD83D\uDE31")
                                animy = "amaze"
                            }
                        }

                        proud1.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE0E")
                                } else {
                                    editText.append("\uD83D\uDE0E")
                                    animy = "proud"
                                }
                            }else {
                                editText.append("\uD83D\uDE0E")
                                animy = "proud"
                            }
                        }
















                        blush1.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83E\uDD2D")
                                } else {
                                    editText.append("\uD83E\uDD2D")
                                    animy = "blush"
                                }
                            }else {
                                editText.append("\uD83E\uDD2D")
                                animy = "blush"
                            }
                        }

                        nointer1.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE0F")
                                } else {
                                    editText.append("\uD83D\uDE0F")
                                    animy = "nointer"
                                }
                            }else {
                                editText.append("\uD83D\uDE0F")
                                animy = "nointer"
                            }
                        }


                        nointer2.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE12")
                                } else {
                                    editText.append("\uD83D\uDE12")
                                    animy = "nointer"
                                }
                            }else {
                                editText.append("\uD83D\uDE12")
                                animy = "nointer"
                            }
                        }

                        nointer3.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE44")
                                } else {
                                    editText.append("\uD83D\uDE44")
                                    animy = "nointer"
                                }
                            }else {
                                editText.append("\uD83D\uDE44")
                                animy = "nointer"
                            }
                        }

                        thank1.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83E\uDD17")
                                } else {
                                    editText.append("\uD83E\uDD17")
                                    animy = "thank"
                                }
                            }else {
                                editText.append("\uD83E\uDD17")
                                animy = "thank"
                            }
                        }


                        cringe1.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE2C")
                                } else {
                                    editText.append("\uD83D\uDE2C")
                                    animy = "cringe"
                                }
                            }else {
                                editText.append("\uD83D\uDE2C")
                                animy = "cringe"
                            }
                        }


                        cringe2.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE10")
                                } else {
                                    editText.append("\uD83D\uDE10")
                                    animy = "cringe"
                                }
                            }else {
                                editText.append("\uD83D\uDE10")
                                animy = "cringe"
                            }
                        }



                        cringe3.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE11")
                                } else {
                                    editText.append("\uD83D\uDE11")
                                    animy = "cringe"
                                }
                            }else {
                                editText.append("\uD83D\uDE11")
                                animy = "cringe"
                            }
                        }

                        yawn1.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83E\uDD71")
                                } else {
                                    editText.append("\uD83E\uDD71")
                                    animy = "pray"
                                }
                            }else {
                                editText.append("\uD83E\uDD71")
                                animy = "pray"
                            }
                        }

                        yawn2.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE2A")
                                } else {
                                    editText.append("\uD83D\uDE2A")
                                    animy = "pray"
                                }
                            }else {
                                editText.append("\uD83D\uDE2A")
                                animy = "pray"
                            }
                        }





                        yawn3.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE34")
                                } else {
                                    editText.append("\uD83D\uDE34")
                                    animy = "pray"
                                }
                            }else {
                                editText.append("\uD83D\uDE34")
                                animy = "pray"
                            }
                        }

                        celeb1.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83E\uDD29")
                                } else {
                                    editText.append("\uD83E\uDD29")
                                    animy = "celebration"
                                }
                            } else {
                                editText.append("\uD83E\uDD29")
                                animy = "celebration"
                            }
                        }


                        celeb2.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83E\uDD73")
                                } else {
                                    editText.append("\uD83E\uDD73")
                                    animy = "celebration"
                                }
                            }else {
                                editText.append("\uD83E\uDD73")
                                animy = "celebration"
                            }
                        }


                        devilangry1.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDC7F")
                                } else {
                                    editText.append("\uD83D\uDC7F")
                                    animy = "angry"
                                }
                            } else {
                                editText.append("\uD83D\uDC7F")
                                animy = "angry"
                            }
                        }


                        danav1.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDC7A")
                                } else {
                                    editText.append("\uD83D\uDC7A")
                                    animy = "angry"
                                }
                            } else {
                                editText.append("\uD83D\uDC7A")
                                animy = "angry"
                            }
                        }

                        fire.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDD25")
                                } else {
                                    editText.append("\uD83D\uDD25")
                                    animy = "thumbup"
                                }
                            } else {
                                editText.append("\uD83D\uDD25")
                                animy = "thumbup"
                            }
                        }


                        hundred.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDCAF")
                                } else {
                                    editText.append("\uD83D\uDCAF")
                                    animy = "thumbup"
                                }
                            } else {
                                editText.append("\uD83D\uDCAF")
                                animy = "thumbup"
                            }
                        }

                        sleepy.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDCA4")
                                } else {
                                    editText.append("\uD83D\uDCA4")
                                    animy = "pray"
                                }
                            } else {
                                editText.append("\uD83D\uDCA4")
                                animy = "pray"
                            }
                        }

                        celeb3.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83C\uDF89")
                                } else {
                                    editText.append("\uD83C\uDF89")
                                    animy = "celebration"
                                }
                            }else {
                                editText.append("\uD83C\uDF89")
                                animy = "celebration"
                            }
                        }

                        monkeyblush1.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE48")
                                } else {
                                    editText.append("\uD83D\uDE48")
                                    animy = "blush"
                                }
                            }else {
                                editText.append("\uD83D\uDE48")
                                animy = "blush"
                            }
                        }



                        monkeyblush2.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE4A")
                                } else {
                                    editText.append("\uD83D\uDE4A")
                                    animy = "blush"
                                }
                            }else {
                                editText.append("\uD83D\uDE4A")
                                animy = "blush"
                            }
                        }

                        catlaughing1.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE3A")
                                } else {
                                    editText.append("\uD83D\uDE3A")
                                    animy = "smile"
                                }
                            } else {
                                editText.append("\uD83D\uDE3A")
                                animy = "smile"
                            }
                        }

                        catlaughing2.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE38")
                                } else {
                                    editText.append("\uD83D\uDE38")
                                    animy = "smile"
                                }
                            }else {
                                editText.append("\uD83D\uDE38")
                                animy = "smile"
                            }
                        }

                        catlaughing3.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE39")
                                } else {
                                    editText.append("\uD83D\uDE39")
                                    animy = "smile"
                                }
                            } else {
                                editText.append("\uD83D\uDE39")
                                animy = "smile"
                            }
                        }

                        catlove1.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE3B")
                                } else {
                                    editText.append("\uD83D\uDE3B")
                                    animy = "happy"
                                }
                            } else {
                                editText.append("\uD83D\uDE3B")
                                animy = "happy"
                            }
                        }


                        catnointer.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE3C")

                                } else {
                                    editText.append("\uD83D\uDE3C")
                                    animy = "nointer"
                                }
                            } else {
                                editText.append("\uD83D\uDE3C")
                                animy = "nointer"
                            }
                        }


                        catkiss1.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE3D")
                                } else {
                                    editText.append("\uD83D\uDE3D")
                                    animy = "kiss"
                                }
                            } else {
                                editText.append("\uD83D\uDE3D")
                                animy = "kiss"
                            }

                        }

                        catshock1.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE40")
                                } else {
                                    editText.append("\uD83D\uDE40")
                                    animy = "amaze"

                                }
                            } else {
                                editText.append("\uD83D\uDE40")
                                animy = "amaze"
                            }
                        }

                        catcry.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE3F")
                                } else {
                                    editText.append("\uD83D\uDE3F")
                                    animy = "sad"
                                }
                            } else {
                                editText.append("\uD83D\uDE3F")
                                animy = "sad"
                            }
                        }
                        catangry.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE3E")
                                } else {
                                    editText.append("\uD83D\uDE3E")
                                    animy = "angry"
                                }
                            } else {
                                editText.append("\uD83D\uDE3E")
                                animy = "angry"
                            }
                        }








                        heart1.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("❤\uFE0F")
                                } else {
                                    editText.append("❤\uFE0F")
                                    animy = "happy"
                                }
                            } else {
                                editText.append("❤\uFE0F")
                                animy = "happy"
                            }
                        }

                        heart2.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDC96")
                                } else {
                                    editText.append("\uD83D\uDC96")
                                    animy = "happy"
                                }
                            } else {
                                editText.append("\uD83D\uDC96")
                                animy = "happy"
                            }

                        }

                        heart3.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDC9F")
                                } else {
                                    editText.append("\uD83D\uDC9F")
                                    animy = "happy"
                                }
                            } else {
                                editText.append("\uD83D\uDC9F")
                                animy = "happy"
                            }
                        }

                        heart4.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDC93")
                                } else {
                                    editText.append("\uD83D\uDC93")
                                    animy = "happy"
                                }
                            } else {
                                editText.append("\uD83D\uDC93")
                                animy = "happy"
                            }
                        }

                        heart5.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDC97")
                                } else {
                                    editText.append("\uD83D\uDC97")
                                    animy = "happy"
                                }
                            } else {
                                editText.append("\uD83D\uDC97")
                                animy = "happy"
                            }
                        }

                        heart6.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDC9E")
                                } else {
                                    editText.append("\uD83D\uDC9E")
                                    animy = "happy"
                                }
                            } else {
                                editText.append("\uD83D\uDC9E")
                                animy = "happy"
                            }
                        }

                        heart7.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDC95")
                                } else {
                                    editText.append("\uD83D\uDC95")
                                    animy = "happy"
                                }
                            } else {
                                editText.append("\uD83D\uDC95")
                                animy = "happy"
                            }
                        }

                        heart8.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("❣\uFE0F")
                                } else {
                                    editText.append("❣\uFE0F")
                                    animy = "happy"
                                }
                            } else {
                                editText.append("❣\uFE0F")
                                animy = "happy"
                            }
                        }


                        heartbreak1.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDC94")
                                } else {
                                    editText.append("\uD83D\uDC94")
                                    animy = "sad"

                                }
                            } else {
                                editText.append("\uD83D\uDC94")
                                animy = "sad"
                            }
                        }


                        lip1.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDC8B")
                                } else {
                                    editText.append("\uD83D\uDC8B")
                                    animy = "kiss"

                                }
                            } else {
                                editText.append("\uD83D\uDC8B")
                                animy = "kiss"
                            }
                        }





                        talk1.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDDE3\uFE0F")
                                } else {
                                    editText.append("\uD83D\uDDE3\uFE0F")
                                    animy = "talk"

                                }
                            } else {
                                editText.append("\uD83D\uDDE3\uFE0F")
                                animy = "talk"
                            }
                        }

                        eye1.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDC40")
                                } else {
                                    editText.append("\uD83D\uDC40")
                                    animy = "amaze"

                                }
                            } else {
                                editText.append("\uD83D\uDC40")
                                animy = "amaze"
                            }
                        }


                        eye2.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDC41\uFE0F")
                                } else {
                                    editText.append("\uD83D\uDC41\uFE0F")
                                    animy = "amaze"

                                }
                            } else {
                                editText.append("\uD83D\uDC41\uFE0F")
                                animy = "amaze"
                            }
                        }

                        strong1.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDCAA")
                                } else {
                                    editText.append("\uD83D\uDCAA")
                                    animy = "victory"
                                }
                            } else {
                                editText.append("\uD83D\uDCAA")
                                animy = "victory"
                            }
                        }

                        strong2.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83E\uDDBE")
                                } else {
                                    editText.append("\uD83E\uDDBE")
                                    animy = "victory"
                                }
                            } else {
                                editText.append("\uD83E\uDDBE")
                                animy = "victory"
                            }
                        }



                        strong3.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("✊")
                                } else {
                                    editText.append("✊")
                                    animy = "victory"

                                }
                            } else {
                                editText.append("✊")
                                animy = "victory"
                            }
                        }






                        thumbup1.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDC4D")
                                } else {
                                    editText.append("\uD83D\uDC4D")
                                    animy = "thumbup"

                                }
                            } else {
                                editText.append("\uD83D\uDC4D")
                                animy = "thumbup"
                            }
                        }

                        thumbdown1.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDC4E")
                                } else {
                                    editText.append("\uD83D\uDC4E")
                                    animy = "thumbdown"

                                }
                            } else {
                                editText.append("\uD83D\uDC4E")
                                animy = "thumbdown"
                            }
                        }

                        hanshake1.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83E\uDD1D")
                                } else {
                                    editText.append("\uD83E\uDD1D")
                                    animy = "handshake"
                                }
                            } else {
                                editText.append("\uD83E\uDD1D")
                                animy = "handshake"
                            }
                        }






                        yo1.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83E\uDD1F")
                                } else {
                                    editText.append("\uD83E\uDD1F")
                                    animy = "dance"
                                }
                            } else {
                                editText.append("\uD83E\uDD1F")
                                animy = "dance"
                            }
                        }

                        yo2.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83E\uDD18")
                                } else {
                                    editText.append("\uD83E\uDD18")
                                    animy = "dance"
                                }
                            } else {
                                editText.append("\uD83E\uDD18")
                                animy = "dance"
                            }
                        }

                        wave1.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDC4B")
                                } else {
                                    editText.append("\uD83D\uDC4B")
                                    animy = "headnod"

                                }
                            } else {
                                editText.append("\uD83D\uDC4B")
                                animy = "headnod"
                            }
                        }

                        wave2.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDD90\uFE0F")
                                } else {
                                    editText.append("\uD83D\uDD90\uFE0F")
                                    animy = "headnod"

                                }
                            } else {
                                editText.append("\uD83D\uDD90\uFE0F")
                                animy = "headnod"
                            }
                        }






                        wave3.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("✋")
                                } else {
                                    editText.append("✋")
                                    animy = "headnod"

                                }
                            } else {
                                editText.append("✋")
                                animy = "headnod"
                            }
                        }

                        pranam1.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE4F")
                                } else {
                                    editText.append("\uD83D\uDE4F")
                                    animy = "clap"
                                }
                            } else {
                                editText.append("\uD83D\uDE4F")
                                animy = "clap"
                            }
                        }

                        insult1.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDD95")
                                } else {
                                    editText.append("\uD83D\uDD95")
                                    animy = "insult"

                                }
                            }else {
                                editText.append("\uD83D\uDD95")
                                animy = "insult"
                            }
                        }







                        bowdown.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDE47")
                                } else {
                                    editText.append("\uD83D\uDE47")
                                    animy = "clap"
                                }
                            } else {
                                editText.append("\uD83D\uDE47")
                                animy = "clap"
                            }
                        }


                        girldance.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDC83")
                                } else {
                                    editText.append("\uD83D\uDC83")
                                    animy = "dance"
                                }
                            }else {
                                editText.append("\uD83D\uDC83")
                                animy = "dance"
                            }

                        }

                        boydance.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("\uD83D\uDD7A")
                                } else {
                                    editText.append("\uD83D\uDD7A")
                                    animy = "dance"
                                }
                            } else {
                                editText.append("\uD83D\uDD7A")
                                animy = "dance"
                            }
                        }

                        right.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("✅")
                                } else {
                                    editText.append("✅")
                                    animy = "thumbup"
                                }
                            } else {
                                editText.append("✅")
                                animy = "thumbup"
                            }
                        }

                        wrong1.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("❌")
                                } else {
                                    editText.append("❌")
                                    animy = "headingno"

                                }
                            } else {
                                editText.append("❌")
                                animy = "headingno"
                            }
                        }

                        wrong2.setOnClickListener {
                            for (i in 0 until editText.text.length) {
                                type = Character.getType(editText.text[i])
                            }
                            if (editText.text.isNotEmpty()) {
                                if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt() || type == Character.NON_SPACING_MARK.toInt()) {
                                    editText.append("❎")
                                } else {
                                    editText.append("❎")
                                    animy = "headingno"

                                }
                            } else {
                                editText.append("❎")
                                animy = "headingno"
                            }
                        }

                        val deltebutton: FloatingActionButton = this.findViewById(R.id.cancel)

                        deltebutton.setOnClickListener {
                            if (editText.text.toString().trim().length == 1) {
                                val result: String = editText.text.toString().substring(0, editText.text.toString().length - 1)
                                editText.setText(result)
                                editText.setSelection(result.length)
                            } else if (editText.text.toString().trim().length > 1){
                                val result: String = editText.text.toString().substring(0, editText.text.toString().length - 2)
                                editText.setText(result)
                                editText.setSelection(result.length)
                            }
                        }
                    }
                    val displayMetrics = resources.displayMetrics
                    val dpValue2 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 222f, displayMetrics)
                   width = ViewGroup.LayoutParams.MATCH_PARENT
                    height = dpValue2.toInt()

                }.also { popupWindow ->
                    // Absolute location of the anchor view
                   val location = IntArray(2).apply {
                        editText.getLocationInWindow(this)
                    }

                    //    val size = Size(popupWindow.contentView.measuredWidth, popupWindow.contentView.measuredHeight)
                    popupWindow.setBackgroundDrawable(ColorDrawable(Color.BLACK))
             }
        }
        }

    }

    override fun onStart() {
        super.onStart()
        active = true


    }

    override fun onStop() {
        super.onStop()
        active = false
    }

   override fun onResume() {
        super.onResume()
       getstatenow(inkid)

    }

    private fun getstatenow(reciveruid: String?) {
        FirebaseDatabase.getInstance().getReference("Usersrooms").child(uid.toString()).child(reciveruid.toString()).addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val bbc =   snapshot.child("isread").value
                if (bbc == false && active == true) {
                    val pj : MutableMap<String, Any> = hashMapOf()
                    pj.put("isread",true)
                    FirebaseDatabase.getInstance().getReference("Usersrooms").child(uid.toString()).child(reciveruid.toString()).updateChildren(pj)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun sendData(p: String, reciveruid: String, aniv: String) {
        val bp =  FirebaseDatabase.getInstance().getReference("Timestamp").child(uid.toString())
        val postValues: MutableMap<String, Any> = hashMapOf()
        postValues.put("timestamp", ServerValue.TIMESTAMP)
        bp.updateChildren(postValues).addOnSuccessListener {
            bp.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val time = snapshot.child("timestamp").value as Long
                    val negativetime = -1 * time
                    val pj : MutableMap<String, Any> = hashMapOf()
                    pj.put("timestamp",negativetime)
                    pj.put("isread",false)
                    pj.put("lastmessage",p)
                    val vp = MessageClass(uid,p,negativetime)
                    FirebaseDatabase.getInstance().getReference("Message").child(senderRoom.toString()).push().setValue(vp).addOnSuccessListener {
                        FirebaseDatabase.getInstance().getReference("Message2").child(senderRoom.toString()).push().setValue(vp).addOnSuccessListener {
                            FirebaseDatabase.getInstance().getReference("Usersrooms").child(reciveruid.toString()).child(uid.toString()).updateChildren(pj)
                            FirebaseDatabase.getInstance().getReference("Usersrooms").child(uid.toString()).child(reciveruid.toString()).updateChildren(pj)
                            animy = "none"
                            if (aniv == "smile"){
                                prolaughingAnimation()
                            } else if (aniv == "sad"){
                                proSadAnimation()
                            } else if (aniv == "happy"){
                                proHappyanimation()
                            } else if (aniv == "cry"){
                                proSadAnimation()
                            } else if (aniv == "amaze"){
                                proReactanimation()
                            } else if (aniv == "salute"){
                                proSalutingAnimation()
                            } else if (aniv == "celebration"){
                                proDancinfAnimation()
                            } else if (aniv == "dance"){
                                proDancinfAnimation()
                            } else if (aniv == "clap"){
                                proClappinganimation()
                            } else if (aniv == "handshake"){
                                proShakingHandAnimations()
                            } else if (aniv == "headingno"){
                                proHeadingnoAnimation()
                            } else if (aniv == "headnod"){
                                proheadnoddingAnimation()
                            } else if (aniv == "pray"){
                                proPrayingAnimation()
                            } else if (aniv == "angry"){
                                proangryAnimation()
                            } else if (aniv == "insult"){
                                proInsultingAnimation()
                            } else if (aniv == "kiss"){
                                proKissingAnimation()
                            } else if (aniv == "victory"){
                                proVictoryAnimation()
                            } else if (aniv == "none"){
                               proTalkAnimation()
                           } else if(aniv == "talk"){
                               proTalkAnimation()
                            } else if(aniv == "blush"){
                                proBlushanimation()
                            } else if (aniv == "nointer"){
                                proInteranimation()
                            }else if (aniv == "thank"){
                                proThankanimation()
                            }else if (aniv == "thumbup"){
                                proThumbUPanimation()
                            }else if (aniv == "thumbdown"){
                                proThumbdownanimation()
                            } else if (aniv == "cringe"){
                                proCringeanimation()
                            } else if (aniv == "proud"){
                                proProudanimation()
                            }

                        }

                    }
                }
               override fun onCancelled(error: DatabaseError) {
                }


            })
        }
        editText.setText("")

    }




    private fun proTalkAnimation() {
        if (valuenow != null){
            if (valuenow != "getTalkingAnimation"){
                UnityPlayer.UnitySendMessage(urlpart,valuenow,"false")
                valuenow = "getTalkingAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getTalkingAnimation","true")
                ctimer!!.start()
            } else {
                valuenow = "getTalkingAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getduptalk","")
                ctimer!!.start()


            }

        }  else {
            valuenow = "getTalkingAnimation"
            UnityPlayer.UnitySendMessage(urlpart,"getTalkingAnimation","true")
            ctimer!!.start()
       }



    }

    private fun prolaughingAnimation() {
        if (valuenow != null){
            if (valuenow != "getLaughingAnimation"){
                UnityPlayer.UnitySendMessage(urlpart,valuenow,"false")
                valuenow = "getLaughingAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getLaughingAnimation","true")
                  ctimer!!.start()

            } else {
                    valuenow = "getLaughingAnimation"
                   UnityPlayer.UnitySendMessage(urlpart,"getduplaugh","")
                     ctimer!!.start()

                  /*  Handler(Looper.getMainLooper()).postDelayed({
                        UnityPlayer.UnitySendMessage(urlpart,"getLaughingAnimation","false")
                    },7000)*/

            }

       }  else {
            valuenow = "getLaughingAnimation"
            UnityPlayer.UnitySendMessage(urlpart,"getLaughingAnimation","true")
            ctimer!!.start()


          /*  Handler(Looper.getMainLooper()).postDelayed({
                UnityPlayer.UnitySendMessage(urlpart,"getLaughingAnimation","false")
            },7000)*/
        }

    }

    private fun proPrayingAnimation() {
        if (valuenow != null){
            if (valuenow != "getPrayingAnimation"){
                UnityPlayer.UnitySendMessage(urlpart,valuenow,"false")
                valuenow = "getPrayingAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getPrayingAnimation","true")
                ctimer!!.start()


            } else {
                valuenow = "getPrayingAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getduppray","")
                ctimer!!.start()


            }

        }  else {
            valuenow = "getPrayingAnimation"
            UnityPlayer.UnitySendMessage(urlpart,"getPrayingAnimation","true")
            ctimer!!.start()
       }
    }

    private fun proSalutingAnimation() {
        if (valuenow != null){
            if (valuenow != "getSalutingAnimation"){
                UnityPlayer.UnitySendMessage(urlpart,valuenow,"false")
                valuenow = "getSalutingAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getSalutingAnimation","true")
                ctimer!!.start()


            } else {
                valuenow = "getSalutingAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getdupsalute","")
                ctimer!!.start()


            }

        }  else {
            valuenow = "getSalutingAnimation"
            UnityPlayer.UnitySendMessage(urlpart,"getSalutingAnimation","true")
            ctimer!!.start()
        }
    }

    private fun proShakingHandAnimations() {
        if (valuenow != null){
            if (valuenow != "getShakinghandsAnimation"){
                UnityPlayer.UnitySendMessage(urlpart,valuenow,"false")
                valuenow = "getShakinghandsAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getShakinghandsAnimation","true")
                ctimer!!.start()


            } else {
                valuenow = "getShakinghandsAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getdupshakehand","")
                ctimer!!.start()


            }

        }  else {
            valuenow = "getShakinghandsAnimation"
            UnityPlayer.UnitySendMessage(urlpart,"getShakinghandsAnimation","true")
            ctimer!!.start()
        }
    }

    private fun proHeadingnoAnimation() {
        if (valuenow != null){
            if (valuenow != "getheadingnoAnimation"){
                UnityPlayer.UnitySendMessage(urlpart,valuenow,"false")
                valuenow = "getheadingnoAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getheadingnoAnimation","true")
                ctimer!!.start()


            } else {
                valuenow = "getheadingnoAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getdupheadno","")
                ctimer!!.start()


            }

        }  else {
            valuenow = "getheadingnoAnimation"
            UnityPlayer.UnitySendMessage(urlpart,"getheadingnoAnimation","true")
            ctimer!!.start()
        }
    }

   private fun proDancinfAnimation() {
        if (valuenow != null){
            if (valuenow != "getDancingAnimation"){
                UnityPlayer.UnitySendMessage(urlpart,valuenow,"false")
                valuenow = "getDancingAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getDancingAnimation","true")
                ctimer!!.start()


            } else {
                valuenow = "getDancingAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getdupdance","")
                ctimer!!.start()


                        }
        }  else {
            valuenow = "getDancingAnimation"
            UnityPlayer.UnitySendMessage(urlpart,"getDancingAnimation","true")
            ctimer!!.start()
        }
    }

    private fun proCryAnimation() {
        if (valuenow != null){
            if (valuenow != "getCryingAnimation"){
                UnityPlayer.UnitySendMessage(urlpart,valuenow,"false")
               valuenow = "getCryingAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getCryingAnimation","true")
                ctimer!!.start()


            } else {
                valuenow = "getCryingAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getdupcry","")
                ctimer!!.start()


            }

        }  else {
            valuenow = "getCryingAnimation"
            UnityPlayer.UnitySendMessage(urlpart,"getCryingAnimation","true")
            ctimer!!.start()
        }
    }

    private fun proVictoryAnimation() {
        if (valuenow != null){
          if (valuenow != "getVictoryAnimation"){
               UnityPlayer.UnitySendMessage(urlpart,valuenow,"false")
               valuenow = "getVictoryAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getVictoryAnimation","true")
              ctimer!!.start()


           } else {
                valuenow = "getVictoryAnimation"
              UnityPlayer.UnitySendMessage(urlpart,"getdupvictory","")
               ctimer!!.start()


           }

       }  else {
            valuenow = "getVictoryAnimation"
           UnityPlayer.UnitySendMessage(urlpart,"getVictoryAnimation","true")
            ctimer!!.start()
        }
    }

   private fun proSadAnimation() {
        if (valuenow != null){
           if (valuenow != "getSadAnimation"){
               UnityPlayer.UnitySendMessage(urlpart,valuenow,"false")
                valuenow = "getSadAnimation"
               UnityPlayer.UnitySendMessage(urlpart,"getSadAnimation","true")
               ctimer!!.start()


           } else {
                valuenow = "getSadAnimation"
               UnityPlayer.UnitySendMessage(urlpart,"getdupsad","")
               ctimer!!.start()


           }

       }  else {
            valuenow = "getSadAnimation"
           UnityPlayer.UnitySendMessage(urlpart,"getSadAnimation","true")
            ctimer!!.start()
        }
    }

    private fun proheadnoddingAnimation() {
        if (valuenow != null){
            if (valuenow != "getHeadnoddingAnimation"){
              UnityPlayer.UnitySendMessage(urlpart,valuenow,"false")
                valuenow = "getHeadnoddingAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getHeadnoddingAnimation","true")
                ctimer!!.start()


            } else {
                valuenow = "getHeadnoddingAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getdupheadnodding","")
                ctimer!!.start()


            }

        }  else {
            valuenow = "getHeadnoddingAnimation"
            UnityPlayer.UnitySendMessage(urlpart,"getHeadnoddingAnimation","true")
            ctimer!!.start()
        }
    }

    private fun proangryAnimation() {
        if (valuenow != null){
            if (valuenow != "getAngryAnimation"){
                UnityPlayer.UnitySendMessage(urlpart,valuenow,"false")
                valuenow = "getAngryAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getAngryAnimation","true")
                ctimer!!.start()


            } else {
               valuenow = "getAngryAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getdupangry","")
                ctimer!!.start()


            }

        }  else {
            valuenow = "getAngryAnimation"
          UnityPlayer.UnitySendMessage(urlpart,"getAngryAnimation","true")
            ctimer!!.start()
       }
        }

    private fun proKissingAnimation() {
        if (valuenow != null){
            if (valuenow != "getKissingAnimation"){
                UnityPlayer.UnitySendMessage(urlpart,valuenow,"false")
                valuenow = "getKissingAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getKissingAnimation","true")
                ctimer!!.start()


            } else {
                valuenow = "getKissingAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getdupkiss","")
                ctimer!!.start()


            }

        }  else {
            valuenow = "getKissingAnimation"
            UnityPlayer.UnitySendMessage(urlpart,"getKissingAnimation","true")
            ctimer!!.start()
        }
    }

    private fun proInsultingAnimation() {
        if (valuenow != null){
            if (valuenow != "getInsultingAnimation"){
                UnityPlayer.UnitySendMessage(urlpart,valuenow,"false")
                valuenow = "getInsultingAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getInsultingAnimation","true")
                ctimer!!.start()


            } else {
                valuenow = "getInsultingAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getdupinsult","")
                ctimer!!.start()


            }

        }  else {
            valuenow = "getInsultingAnimation"
            UnityPlayer.UnitySendMessage(urlpart,"getInsultingAnimation","true")
            ctimer!!.start()
        }
    }


        private fun proClappinganimation() {
        if (valuenow != null){
            if (valuenow != "getClappingAnimation"){
                UnityPlayer.UnitySendMessage(urlpart,valuenow,"false")
               valuenow = "getClappingAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getClappingAnimation","true")
                ctimer!!.start()


            } else {
                valuenow = "getClappingAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getdupclap","")
                ctimer!!.start()


            }

        }  else {
            valuenow = "getClappingAnimation"
            UnityPlayer.UnitySendMessage(urlpart,"getClappingAnimation","true")
            ctimer!!.start()
        }

    }

    private fun proHappyanimation() {
        if (valuenow != null){
        if (valuenow != "getHappyAnimation"){
                UnityPlayer.UnitySendMessage(urlpart,valuenow,"false")
               valuenow = "getHappyAnimation"
               UnityPlayer.UnitySendMessage(urlpart,"getHappyAnimation","true")
               ctimer!!.start()


           } else {
               valuenow = "getHappyAnimation"
            UnityPlayer.UnitySendMessage(urlpart,"getduphappy","")
            ctimer!!.start()


            }

       }  else {
            valuenow = "getHappyAnimation"
            UnityPlayer.UnitySendMessage(urlpart,"getHappyAnimation","true")
            ctimer!!.start()
        }

    }

    private fun proReactanimation() {
        if (valuenow != null){
            if (valuenow != "getReactingAnimation"){
                UnityPlayer.UnitySendMessage(urlpart,valuenow,"false")
               valuenow = "getReactingAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getReactingAnimation","true")
                ctimer!!.start()


            } else {
               valuenow = "getReactingAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getdupreact","")
                ctimer!!.start()


           }

        }  else {
            valuenow = "getReactingAnimation"
            UnityPlayer.UnitySendMessage(urlpart,"getReactingAnimation","true")
            ctimer!!.start()
        }

    }


    private fun proBlushanimation() {
        if (valuenow != null){
            if (valuenow != "getBlushingAnimation"){
                UnityPlayer.UnitySendMessage(urlpart,valuenow,"false")
                valuenow = "getBlushingAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getBlushingAnimation","true")
                ctimer!!.start()


            } else {
                valuenow = "getBlushingAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getdupblush","")
                ctimer!!.start()


            }

        }  else {
            valuenow = "getBlushingAnimation"
            UnityPlayer.UnitySendMessage(urlpart,"getBlushingAnimation","true")
            ctimer!!.start()
        }

    }

    private fun proThumbdownanimation() {
        if (valuenow != null){
            if (valuenow != "getThumbdownAnimation"){
                UnityPlayer.UnitySendMessage(urlpart,valuenow,"false")
                valuenow = "getThumbdownAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getThumbdownAnimation","true")
                ctimer!!.start()


            } else {
                valuenow = "getThumbdownAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getdupthumbdown","")
                ctimer!!.start()


            }

        }  else {
            valuenow = "getThumbdownAnimation"
            UnityPlayer.UnitySendMessage(urlpart,"getThumbdownAnimation","true")
            ctimer!!.start()
        }

    }



    private fun proThumbUPanimation() {
        if (valuenow != null){
            if (valuenow != "getThumbUPAnimation"){
                UnityPlayer.UnitySendMessage(urlpart,valuenow,"false")
                valuenow = "getThumbUPAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getThumbUPAnimation","true")
                ctimer!!.start()


            } else {
                valuenow = "getThumbUPAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getdupthumbup","")
                ctimer!!.start()


            }

        }  else {
            valuenow = "getThumbUPAnimation"
            UnityPlayer.UnitySendMessage(urlpart,"getThumbUPAnimation","true")
            ctimer!!.start()
        }

    }




    private fun proCringeanimation() {
        if (valuenow != null){
            if (valuenow != "getCringeAnimation"){
                UnityPlayer.UnitySendMessage(urlpart,valuenow,"false")
                valuenow = "getCringeAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getCringeAnimation","true")
                ctimer!!.start()


            } else {
                valuenow = "getCringeAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getdupcringe","")
                ctimer!!.start()


            }

        }  else {
            valuenow = "getCringeAnimation"
            UnityPlayer.UnitySendMessage(urlpart,"getCringeAnimation","true")
            ctimer!!.start()
        }

    }

    private fun proThankanimation() {
        if (valuenow != null){
            if (valuenow != "getThankAnimation"){
                UnityPlayer.UnitySendMessage(urlpart,valuenow,"false")
                valuenow = "getThankAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getThankAnimation","true")
                ctimer!!.start()


            } else {
                valuenow = "getThankAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getdupthank","")
                ctimer!!.start()


            }

        }  else {
            valuenow = "getThankAnimation"
            UnityPlayer.UnitySendMessage(urlpart,"getThankAnimation","true")
            ctimer!!.start()
        }

    }

    private fun proInteranimation() {
        if (valuenow != null){
            if (valuenow != "getInterAnimation"){
                UnityPlayer.UnitySendMessage(urlpart,valuenow,"false")
                valuenow = "getInterAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getInterAnimation","true")
                ctimer!!.start()


            } else {
                valuenow = "getInterAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getdupinter","")
                ctimer!!.start()


            }

        }  else {
            valuenow = "getInterAnimation"
            UnityPlayer.UnitySendMessage(urlpart,"getInterAnimation","true")
            ctimer!!.start()
        }

    }

    private fun proProudanimation() {
        if (valuenow != null){
            if (valuenow != "getProudAnimation"){
                UnityPlayer.UnitySendMessage(urlpart,valuenow,"false")
                valuenow = "getProudAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getProudAnimation","true")
                ctimer!!.start()


            } else {
                valuenow = "getProudAnimation"
                UnityPlayer.UnitySendMessage(urlpart,"getdupproud","")
                ctimer!!.start()


            }

        }  else {
            valuenow = "getProudAnimation"
            UnityPlayer.UnitySendMessage(urlpart,"getProudAnimation","true")
            ctimer!!.start()
        }

    }

    private fun ShineAnimation() {
        val animation: Animation = TranslateAnimation(0f,
            (img.getWidth() + shine.getWidth()).toFloat(), 0f, 0f)
        animation.duration = 550
        animation.fillAfter = false
        animation.interpolator = AccelerateDecelerateInterpolator()
        animation.setRepeatCount(-1)
        shine.startAnimation(animation)
    }





    private fun getDatareciverroom() {
        FirebaseDatabase.getInstance().getReference("Message2").child(reciverRoom.toString()).orderByChild("timestamp")
            .limitToFirst(15).addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                   mlist.clear()
                    for (datasnap in snapshot.children){
                        val bc = datasnap.getValue(MessageClass::class.java)
                        mlist.add(bc!!)
                    }
                  BAdapter.addlisttop(mlist)
                    recyclerView.adapter?.let { recyclerView.smoothScrollToPosition(0) }
                   BAdapter.notifyDataSetChanged()

               }

               override fun onCancelled(error: DatabaseError) {

                }


           })

    }

    private fun getDatafromDatabase() {
       FirebaseDatabase.getInstance().getReference("Message").child(senderRoom.toString())
            .orderByChild("timestamp").limitToFirst(15).addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                   ulist.clear()
                    for (datasnap in snapshot.children) {
                        val bc = datasnap.getValue(MessageClass::class.java)
                        ulist.add(bc!!)
                    }
                   if (ulist.size > 0) {
                        //    key = ulist[ulist.size - 1].timestamp
                   }
                    chatAdapter.addlisttop(ulist)
                    recylerview2.adapter?.let { recylerview2.smoothScrollToPosition(0) }
                    chatAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })


    }









}
