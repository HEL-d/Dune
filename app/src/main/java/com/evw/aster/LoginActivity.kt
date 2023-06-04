package com.evw.aster
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.smb.glowbutton.GlowButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class LoginActivity : AppCompatActivity() {
    lateinit var internetConnectivity: InternetConnectivity
    lateinit var button: GlowButton
    lateinit var materialEditText: EditText
    lateinit var viewDialog: ViewDialog
    lateinit var networkbox: Networkbox
    lateinit var mAuth:FirebaseAuth
    lateinit var name:String
    lateinit var birthday:String
    lateinit var username:String
    lateinit var storedVerificationId:String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    lateinit var progressbarDialog: Dialog
    lateinit var exceptiondialogclass: exceptiondialogclass
    override  fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        button = findViewById(R.id.button_phone)
        mAuth = FirebaseAuth.getInstance()
        internetConnectivity = InternetConnectivity()
        materialEditText = findViewById(R.id.number)
        viewDialog = ViewDialog()
        networkbox = Networkbox()
        exceptiondialogclass = exceptiondialogclass()
        progressbarDialog = Dialog(this)
        initDialog()
        name = intent.getStringExtra("name").toString()
        birthday = intent.getStringExtra("birthday").toString()
        username = intent.getStringExtra("username").toString()
        button.setOnClickListener {
            if (materialEditText.text?.isEmpty() == true){
            viewDialog.showDialog(this,"Enter your Phone number first","We need number for verification purpose")
         } else{
           internetConnectivity.checkInternetConnection(object:InternetConnectivity.ConnectivityCallback{
               override fun onDetected(isConnected: Boolean) {
                   if (isConnected){
                       progressbarDialog.show()
                       CoroutineScope(Dispatchers.IO).launch {
                           val num = "+91"+ materialEditText.text.toString()
                           checktoDatabase(num,mAuth,materialEditText.text.toString())
                       }
                   } else {
                       networkbox.shownetworkdialog(this@LoginActivity)
                   }

               }

           },this)

         }


        }
    }

    private fun initDialog() {
        progressbarDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressbarDialog.setCancelable(false)
        progressbarDialog.setContentView(R.layout.progressdialog)
    }


    private suspend fun checktoDatabase(num: String, mAuth: FirebaseAuth, dbnum: String): QuerySnapshot? {
        return try {
            val doc = FirebaseFirestore.getInstance().collection("Contacts").whereEqualTo("phoneNumber",dbnum).get().await()
            if(!doc.isEmpty){
                withContext(Dispatchers.Main){
                    progressbarDialog.dismiss()
                    viewDialog.showDialog(this@LoginActivity,"This number is already registered !!","Try to Login account in Login section")
                }

            } else {
                verifynumber(num, mAuth)
            }
            doc
        } catch (e:Exception){
            withContext(Dispatchers.Main){
                progressbarDialog.dismiss()
                viewDialog.showDialog(this@LoginActivity,"Failed to fetch data from database","Please try again letter to proceed !!")
            }
            null
        }



    }

















    sealed class PhoneAuthResult {
        data class VerificationCompleted(val credentials: PhoneAuthCredential) : PhoneAuthResult()
        data class CodeSent(val verificationId: String, val token: PhoneAuthProvider.ForceResendingToken)
            : PhoneAuthResult()
    }


    private suspend fun verifynumber(num: String, mAuth: FirebaseAuth):PhoneAuthResult = suspendCoroutine { cont ->
        val callback = object:PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
             cont.resume(PhoneAuthResult.VerificationCompleted(p0))
            }
            override fun onVerificationFailed(p0: FirebaseException) {
                CoroutineScope(Dispatchers.Main).launch {
                    progressbarDialog.dismiss()
                    exceptiondialogclass.shoDialog(this@LoginActivity)
                }
           //   cont.resumeWithException(p0)


            }
            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                cont.resume(PhoneAuthResult.CodeSent(p0,p1))
                storedVerificationId = p0
                resendToken = p1
                CoroutineScope(Dispatchers.Main).launch {
                    progressbarDialog.dismiss()
                    startnow(storedVerificationId,resendToken,num)
                }




            }
        }

        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(num)
            .setTimeout(60L,TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callback)
            .build()
         PhoneAuthProvider.verifyPhoneNumber(options)



    }




    private  fun startnow(
        storedVerificationId: String,
        resendToken: PhoneAuthProvider.ForceResendingToken,
        num: String) {
        val intent = Intent(Intent(this@LoginActivity,OTPActivity::class.java))
                intent.putExtra("storedverificationID",storedVerificationId)
             intent.putExtra("name",name)
              intent.putExtra("Tokennow",resendToken)
               intent.putExtra("birthday",birthday)
              intent.putExtra("username",username)
              intent.putExtra("phoneNumber",num)
               startActivity(intent)
    }









}