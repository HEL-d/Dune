package com.evw.aster

import android.annotation.SuppressLint

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
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
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
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



class GameActivity:UnityPlayerActivity() {

      companion object{
          private const val TAG = "GameActivity"
      }

    

    lateinit var relativeLayout2: RelativeLayout
    lateinit var button: RelativeLayout
    lateinit var linerlayout: LinearLayout
    var xdown = 0f
    var ydown:Float = 0f
    var givemedistance:Int = 0
    val uid = FirebaseAuth.getInstance().currentUser?.uid
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
    var flag:Boolean = true
    lateinit var linearLayout506: LinearLayout
    lateinit var textView506: TextView
     var resumecount:Int = 0
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
               editText = findViewById<EditText>(R.id.edittextm)
               linearLayout506 = findViewById(R.id.gamepro)
               textView506 = findViewById(R.id.trip)
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
               if (mich == "null") {
                   Handler(Looper.getMainLooper()).postDelayed({
                       linearLayout506.visibility = View.VISIBLE
                        textView506.setText("Preparing game for you")
                                                               },5000)

                   Handler(Looper.getMainLooper()).postDelayed({
                       textView506.setText("Almost prepared")
                   },12000)


                   Handler(Looper.getMainLooper()).postDelayed({
                       linearLayout506.visibility = View.GONE
                       relativeLayout2.visibility = View.VISIBLE
                       linerlayout.visibility = View.VISIBLE
                       UnityPlayer.UnitySendMessage("Road","activatenow","")
                       UnityPlayer.UnitySendMessage("mask","maskme","")
                   }, 15000)

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
                   },15000)
               } else if (mich != "null"){
                   linerlayout.visibility = View.VISIBLE
                   linerlayout.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                   linerlayout.layoutParams.height = 5
                   Handler(Looper.getMainLooper()).postDelayed({
                       getDatafromDatabase()
                       getDatareciverroom()
                       relativeLayout12.visibility = View.VISIBLE
                   },5000)
                 /*  FirebaseFirestore.getInstance().collection("Triggers").document(uid.toString()).collection(reciveruid.toString()).addSnapshotListener(object:
                       EventListener<QuerySnapshot> {
                       override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                           if (value != null){
                               for (document in value.documentChanges){
                                   if (document.type == DocumentChange.Type.ADDED){
                                       Handler(Looper.getMainLooper()).postDelayed({
                                           Toast.makeText(context,"yes",Toast.LENGTH_SHORT).show()
                                           UnityPlayer.UnitySendMessage("CreateRoomMenu", "getroomname", rm)
                                           UnityPlayer.UnitySendMessage("CreateRoomMenu", "getavatarurl", pm)

                                        //   UnityPlayer.UnitySendMessage("Camera","searchmyavatar",urlpart + '_'+ reciverUrl)
                                       },5000)
                                   } else if (document.type == DocumentChange.Type.MODIFIED){
                                       Handler(Looper.getMainLooper()).postDelayed({
                                           Toast.makeText(context,"yes",Toast.LENGTH_SHORT).show()
                                           UnityPlayer.UnitySendMessage("CreateRoomMenu", "getroomname", rm)
                                           UnityPlayer.UnitySendMessage("CreateRoomMenu", "getavatarurl", pm)

                                       //    UnityPlayer.UnitySendMessage("Camera","searchmyavatar",urlpart + '_'+ reciverUrl)
                                       },5000)
                                   }

                               }
                           }
                       }

                   })*/

                   Handler(Looper.getMainLooper()).postDelayed({
                       val city = hashMapOf("trigger" to FieldValue.serverTimestamp())
                       FirebaseFirestore.getInstance().collection("Triggers").document(reciveruid.toString()).collection(uid.toString()).document("data").set(city).addOnSuccessListener{
                           FirebaseFirestore.getInstance().collection("Triggers").document(uid.toString()).collection(reciveruid.toString()).document("data").set(city)
                       }
                   },1000)

               }





               FirebaseDatabase.getInstance().getReference("Usersrooms").child(uid.toString()).child(inkid.toString()).addValueEventListener(object:ValueEventListener{
                   override fun onDataChange(snapshot: DataSnapshot) {
                       if (snapshot.exists()){
                           val typostatus = snapshot.child("typing").value.toString()
                               if (typostatus == "Typing"){

                               } else {

                               }




                       }




                   }

                   override fun onCancelled(error: DatabaseError) {

                   }

               })







               imageButton1.setOnClickListener {
                   if (editText.text.isNotEmpty()){
                       if(animy == "none"){
                            sendData(editText.text.toString(),reciveruid.toString(),"none")
                        } else if (animy == "smile") {
                           sendData(editText.text.toString(),reciveruid.toString(),"smile")
                        } else if (animy == "happy") {
                           sendData(editText.text.toString(), reciveruid.toString(), "happy")
                       }

                    } else {
                       UnityPlayer.UnitySendMessage("RoomActionCanvas","OnClick_Leave","")
                       Handler(Looper.getMainLooper()).postDelayed({
                           val intent = Intent(this@GameActivity, MainActivity::class.java)
                           intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                           startActivity(intent)




                                                                   },500)

                   }

               }

               
            editText.addTextChangedListener(object:TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                   if (s.toString().trim().length > 0){
                       imageButton1.setBackgroundResource(R.drawable.arrow)
                       checkTypingStatus("Typing")

                   } else {
                       imageButton1.setBackgroundResource(R.drawable.paper)
                       checkTypingStatus("noOne")
                   }
                }

                override fun afterTextChanged(s: Editable?) {

                }


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

    private fun checkTypingStatus(status: String?) {
        val tron : MutableMap<String, Any> = hashMapOf()
        tron.put("typing",status!!)
        FirebaseDatabase.getInstance().getReference("Usersrooms").child(inkid.toString()).child(uid.toString()).updateChildren(tron)

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
       resumecount++
       val nm = intent.getStringExtra("roomname")
       Handler(Looper.getMainLooper()).postDelayed({
           val city = hashMapOf("trigger" to FieldValue.serverTimestamp())
           FirebaseFirestore.getInstance().collection("Triggers").document(inkid.toString()).collection(uid.toString()).document("data").set(city).addOnSuccessListener{
               FirebaseFirestore.getInstance().collection("Triggers").document(uid.toString()).collection(inkid.toString()).document("data").set(city)
           }
       },1000)

       FirebaseFirestore.getInstance().collection("Triggers").document(uid.toString()).collection(inkid.toString()).addSnapshotListener(object:
           EventListener<QuerySnapshot> {
           override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
               if (value != null){
                   for (document in value.documentChanges){
                       if (document.type == DocumentChange.Type.ADDED && resumecount > 1){
                           UnityPlayer.UnitySendMessage("CreateRoomMenu", "getroomname", nm)
                            Toast.makeText(this@GameActivity,resumecount.toString(),Toast.LENGTH_SHORT).show()

                               //   UnityPlayer.UnitySendMessage("Camera","searchmyavatar",urlpart + '_'+ reciverUrl)

                       } else if (document.type == DocumentChange.Type.MODIFIED && resumecount > 1){
                           UnityPlayer.UnitySendMessage("CreateRoomMenu", "getroomname", nm)
                           Toast.makeText(this@GameActivity,resumecount.toString(),Toast.LENGTH_SHORT).show()

                               //    UnityPlayer.UnitySendMessage("Camera","searchmyavatar",urlpart + '_'+ reciverUrl)

                       }

                   }
               }
           }

       })



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
                              //  prolaughingAnimation()
                            } else if (aniv == "sad"){
                             //   proSadAnimation()
                            } else if (aniv == "happy"){
                              //  proHappyanimation()
                            } else if (aniv == "cry"){
                             //   proSadAnimation()
                            } else if (aniv == "amaze"){
                             //   proReactanimation()
                            } else if (aniv == "salute"){
                            //    proSalutingAnimation()
                            } else if (aniv == "celebration"){
                              //  proDancinfAnimation()
                            } else if (aniv == "dance"){
                              //  proDancinfAnimation()
                            } else if (aniv == "clap"){
                              //  proClappinganimation()
                            } else if (aniv == "handshake"){
                             //   proShakingHandAnimations()
                            } else if (aniv == "headingno"){
                            //    proHeadingnoAnimation()
                            } else if (aniv == "headnod"){
                             //   proheadnoddingAnimation()
                            } else if (aniv == "pray"){
                             //   proPrayingAnimation()
                            } else if (aniv == "angry"){
                            //    proangryAnimation()
                            } else if (aniv == "insult"){
                             //   proInsultingAnimation()
                            } else if (aniv == "kiss"){
                             //   proKissingAnimation()
                            } else if (aniv == "victory"){
                             //   proVictoryAnimation()
                            } else if (aniv == "none"){
                               proTalkAnimation()
                           } else if(aniv == "talk"){
                               proTalkAnimation()
                            } else if(aniv == "blush"){
                              //  proBlushanimation()
                            } else if (aniv == "nointer"){
                              //  proInteranimation()
                            }else if (aniv == "thank"){
                             //   proThankanimation()
                            }else if (aniv == "thumbup"){
                              //  proThumbUPanimation()
                            }else if (aniv == "thumbdown"){
                               // proThumbdownanimation()
                            } else if (aniv == "cringe"){
                             //   proCringeanimation()
                            } else if (aniv == "proud"){
                              //  proProudanimation()
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
