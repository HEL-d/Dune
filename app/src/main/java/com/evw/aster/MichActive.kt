package com.evw.aster

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.plusAssign
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.unity3d.player.UnityPlayer
import com.unity3d.player.UnityPlayerActivity


class MichActive:UnityPlayerActivity() {

    lateinit var relativeLayout2: RelativeLayout
    lateinit var linerlayout: LinearLayout
    lateinit var button: RelativeLayout
    lateinit var constraintLayout: RelativeLayout
    lateinit var imageView:ImageView
    var xdown = 0f
    var ydown: Float = 0f
    var givemedistance: Int = 0
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    var count: Int = 0
    lateinit var img:ImageView
    lateinit var shine:ImageView
    lateinit var linearLayout506: LinearLayout
    lateinit var textView506: TextView

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rm = intent.getStringExtra("ppc")
        val pm = intent.getStringExtra("url")
        UnityPlayer.UnitySendMessage("CreateRoomMenu", "getroomname", rm)
        UnityPlayer.UnitySendMessage("CreateRoomMenu", "getavatarurl", pm)
        mUnityPlayer += Gamev(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            ).apply {
                constraintLayout = findViewById(R.id.rootview)
                relativeLayout2 = findViewById(R.id.button28)
                linearLayout506 = findViewById(R.id.gamepro)
                textView506 = findViewById(R.id.trip)
                button = findViewById(R.id.savebutton)
                linerlayout = findViewById(R.id.ncc)
                imageView = findViewById(R.id.fringer)
                img = findViewById(R.id.img)
                shine = findViewById(R.id.shine)
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
                            imageView.visibility = View.VISIBLE
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
                            imageView.setAnimation(mAnimation)
                        },15000)




















                relativeLayout2.setOnTouchListener(View.OnTouchListener({ v, event ->
                    imageView.clearAnimation()
                    imageView.visibility = View.GONE
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
                            val distanceY: Float = movedY - ydown
                            linerlayout.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                            linerlayout.layoutParams.height =
                                (v.y.toInt() + distanceY).toInt() + v.height
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

                 //   UnityPlayer.UnitySendMessage("cameractivation","getangle","")


                    FirebaseFirestore.getInstance().collection("Users").document(uid.toString())
                        .get().addOnCompleteListener {
                            val mich = it.result.get("michHeight")
                            if (mich != null) {
                                FirebaseFirestore.getInstance().collection("Users")
                                    .document(uid.toString())
                                    .update("michHeight", givemedistance.toString())
                                    .addOnSuccessListener {
                                        relativeLayout2.visibility = View.GONE
                                        Toast.makeText(
                                            context,
                                            "Successfully Updated",
                                            Toast.LENGTH_LONG
                                        ).show()

                                        UnityPlayer.UnitySendMessage(
                                            "RoomActionCanvas",
                                            "OnClick_Leave",
                                            ""
                                        )
                                        Handler(Looper.getMainLooper()).postDelayed({
                                            mUnityPlayer.destroy()
                                        },500)


                                       /* Handler(Looper.getMainLooper()).postDelayed({
                                            UnityPlayer.UnitySendMessage("backpoc", "QuitApp", "")
                                        }, 500)*/
                                    }
                            } else {
                                relativeLayout2.visibility = View.GONE
                                Toast.makeText(
                                    context,
                                    "Update this from Mich",
                                    Toast.LENGTH_LONG
                                ).show()
                                UnityPlayer.UnitySendMessage(
                                    "RoomActionCanvas",
                                    "OnClick_Leave",
                                    ""
                                )
                                Handler(Looper.getMainLooper()).postDelayed({
                                    UnityPlayer.UnitySendMessage("backpoc", "QuitApp", "")
                                }, 500)
                            }
                        }


                }




            }


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

}





