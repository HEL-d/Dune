package com.evw.aster
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.firestore.FirebaseFirestore
import com.smb.glowbutton.GlowButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class OTPActivity : AppCompatActivity() {
    lateinit var internetConnectivity: InternetConnectivity
    lateinit var materialEditText: EditText
    lateinit var button: GlowButton
    lateinit var viewDialog: ViewDialog
    lateinit var networkbox: Networkbox
    lateinit var mAuth: FirebaseAuth
    lateinit var name:String
    lateinit var birthday:String
    lateinit var username:String
    lateinit var mVerificationId: String
     lateinit var mResendToken: PhoneAuthProvider.ForceResendingToken
      lateinit var phoneNumber:String
     lateinit var baiscaster: Baiscaster
     lateinit var users: Users
     lateinit var countText: TextView
      lateinit var textView: TextView
      lateinit var progressbardialog:Dialog
      lateinit var exceptiondialogclass: exceptiondialogclass
      lateinit var newbox: newbox
    var originalnumber:String? = null
 //   private val VERIFICATION_ID_KEY = "verification_id"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otpactivity)
        materialEditText = findViewById(R.id.otp)
      textView = findViewById(R.id.resend)
        countText = findViewById(R.id.count_down)
        button = findViewById(R.id.button)
        mAuth = FirebaseAuth.getInstance()
        internetConnectivity = InternetConnectivity()
        viewDialog = ViewDialog()
        networkbox = Networkbox()
        baiscaster = Baiscaster()
       progressbardialog = Dialog(this)
      exceptiondialogclass = exceptiondialogclass()
      newbox = newbox()
      initDialog()
         textView.isEnabled = false
         textView.setTextColor(Color.GRAY)
     initTimer()
          users = Users()
     mVerificationId = intent.getStringExtra("storedverificationID").toString()
     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
         mResendToken = intent.getParcelableExtra("Tokennow",ForceResendingToken::class.java)!!
     } else {
         mResendToken = intent.getParcelableExtra("Tokennow")!!
     }
        name = intent.getStringExtra("name").toString()
       birthday = intent.getStringExtra("birthday").toString()
       username = intent.getStringExtra("username").toString()
       phoneNumber = intent.getStringExtra("phoneNumber").toString()
       originalnumber = phoneNumber.substring(3)
       textView.setOnClickListener {
           internetConnectivity.checkInternetConnection(object:InternetConnectivity.ConnectivityCallback{
               override fun onDetected(isConnected: Boolean) {
                   if (isConnected){
                       progressbardialog.show()
                       CoroutineScope(Dispatchers.IO).launch {
                           resendCode(phoneNumber,mAuth,mResendToken)
                       }

                   } else {
                       networkbox.shownetworkdialog(this@OTPActivity)
                   }
               }

           },this)
       }
     button.setOnClickListener {
            if (materialEditText.text?.isEmpty() == true){
                viewDialog.showDialog(this,"Enter your code first","We need code for verification purpose")
            } else{
                internetConnectivity.checkInternetConnection(object:InternetConnectivity.ConnectivityCallback{
                    override fun onDetected(isConnected: Boolean) {
                        if (isConnected){
                            progressbardialog.show()
                            CoroutineScope(Dispatchers.IO).launch {
                                val code = materialEditText.text.toString()
                                verifyOtp(code,mVerificationId)
                            }

                        } else {
                            networkbox.shownetworkdialog(this@OTPActivity)
                        }
                    }

                },this)
            }
        }
    }

    private fun initDialog() {
        progressbardialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressbardialog.setCancelable(false)
        progressbardialog.setContentView(R.layout.progressdialog)

    }

    sealed class PhoneAuthResult {
        data class VerificationCompleted(val credentials: PhoneAuthCredential) : PhoneAuthResult()
        data class CodeSent(val verificationId: String, val token: PhoneAuthProvider.ForceResendingToken)
            : PhoneAuthResult()
    }

    private suspend fun resendCode(num: String, mAuth: FirebaseAuth, mResendToken: ForceResendingToken): PhoneAuthResult = suspendCoroutine { cont ->
        val callback = object:PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                cont.resume(PhoneAuthResult.VerificationCompleted(p0))
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                CoroutineScope(Dispatchers.Main).launch {
                    progressbardialog.dismiss()
                    exceptiondialogclass.shoDialog(this@OTPActivity)
                }

            //  cont.resumeWithException(p0)

            }

            override fun onCodeSent(p3: String, p1: ForceResendingToken) {
                cont.resume(PhoneAuthResult.CodeSent(p3,p1))
                mVerificationId = p3
                CoroutineScope(Dispatchers.Main).launch {
                    textView.isEnabled = false
                    textView.setTextColor(Color.GRAY)
                    progressbardialog.dismiss()
                    initTimer()
                }

            }
        }
        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(num)
            .setTimeout(60L,TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callback)
            .setForceResendingToken(mResendToken)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }



    private fun initTimer() {
       val cTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countText.setText("00:"+ (millisUntilFinished / 1000).toString())
            }
           override fun onFinish() {
               textView.isEnabled = true
               textView.setTextColor(Color.CYAN)
           }
        }
       cTimer.start()
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putString(VERIFICATION_ID_KEY,mVerificationId)
//    }





    private suspend fun verifyOtp(code: String, mVerificationId: String) {
          val credentials:PhoneAuthCredential = PhoneAuthProvider.getCredential(mVerificationId,code)
         signInWithPhoneAuthCredential(credentials)

    }

    private suspend fun signInWithPhoneAuthCredential(credentials: PhoneAuthCredential):AuthResult? {
        return try {
            val result = mAuth.signInWithCredential(credentials).await()
            updateUI(result,mVerificationId)
            result
        } catch (e:Exception){
            withContext(Dispatchers.Main){
                progressbardialog.dismiss()
                viewDialog.showDialog(this@OTPActivity,"OTP you entered is invalid","Please Fill correct code to proceed further")
                Toast.makeText(this@OTPActivity,"OTP is not valid",Toast.LENGTH_SHORT).show()
            }
            null
        }

    }








    private suspend fun updateUI(result: AuthResult, mVerificationId: String):Boolean {
        val uid = result.user?.uid.toString()
        val docdata = Baiscaster(name, birthday, username, uid,phoneNumber)
        return try {
            val result = FirebaseFirestore.getInstance().collection("Users").document(uid).set(docdata).await()
            updatenew(uid)
            true

        } catch (e:Exception){
            withContext(Dispatchers.Main){
                newbox.shoDialog(this@OTPActivity)
                Toast.makeText(this@OTPActivity,"Failed to save",Toast.LENGTH_SHORT).show()
            }
            false
        }
    }


    private suspend fun updatenew(uid: String):Boolean {
        val doc = fat(username, uid,name)
        return try {
            val result = FirebaseFirestore.getInstance().collection("Usersname").document(username).set(doc).await()
            newUI(uid)
            true
        } catch (e:Exception){
            withContext(Dispatchers.Main){
                newbox.shoDialog(this@OTPActivity)
                Toast.makeText(this@OTPActivity,"Failed to save",Toast.LENGTH_SHORT).show()
            }
            false
        }



    }

    private suspend fun newUI(uid: String):Boolean {
        val num = phoneNumber.substring(3)
        val poc = Users(num,uid,username)
        return try {
            val result = FirebaseFirestore.getInstance().collection("Contacts").document(num).set(poc).await()
            finalUI()
            true
        } catch (e:Exception){
            withContext(Dispatchers.Main){
                newbox.shoDialog(this@OTPActivity)
                Toast.makeText(this@OTPActivity,"Failed to save",Toast.LENGTH_SHORT).show()
            }
            false
        }

    }

    private suspend fun finalUI() {
        withContext(Dispatchers.Main){
            progressbardialog.dismiss()
           val intent:Intent = Intent(applicationContext,avatargen::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("avatarbool","false")
            intent.putExtra("username",username)
            intent.putExtra("phonenumber",originalnumber)
            startActivity(intent)
            finish()

        }
    }


}