package com.evw.aster

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class GooglesignInActivity : AppCompatActivity() {
    private lateinit var edtEmailId : EditText
    private lateinit var edtPassword : EditText
    private lateinit var btnSignUp : Button
    private lateinit var btnSignIn : Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var progressBar: ProgressBar



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_googlesign_in)
        edtEmailId = findViewById(R.id.edtEmailID)
        edtPassword = findViewById(R.id.edtPassword)
        btnSignUp = findViewById(R.id.btnSignUp)
        btnSignIn = findViewById(R.id.btnSignIn)
        progressBar = findViewById(R.id.progress_circular)
        mAuth = FirebaseAuth.getInstance()
        btnSignUp.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            val emailId = edtEmailId.text.toString()
            val password = edtPassword.text.toString()
            lifecycleScope.launch(Dispatchers.IO){
                signUpWithEmailAndPassword(mAuth,emailId,password)
            }
        }
        btnSignIn.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            val emailId = edtEmailId.text.toString()
            val password = edtPassword.text.toString()
            lifecycleScope.launch(Dispatchers.IO){
                signInWithEmailAndPassword(mAuth,emailId,password)
            }
        }
    }

    suspend fun signUpWithEmailAndPassword(
        firebaseAuth: FirebaseAuth,
        emailId: String,
        password: String) : AuthResult? {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(emailId,password).await()
            updateUI(result.user)
            result
        } catch (e :Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@GooglesignInActivity, "${e.message}", Toast.LENGTH_SHORT).show()
                Log.d("AuthResult","${e.message}")
                progressBar.visibility = View.GONE
            }
            null
        }
    }
    suspend fun signInWithEmailAndPassword(
        firebaseAuth: FirebaseAuth,
        emailId: String,
        password: String) :AuthResult? {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(emailId,password)
                .await()
            updateUI(result.user)
            result
        } catch (e :Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@GooglesignInActivity, "${e.message}", Toast.LENGTH_SHORT).show()
                Log.d("AuthResult","${e.message}")
                progressBar.visibility = View.GONE
            }
            null
        }
    }
    private suspend fun updateUI(firebaseUser : FirebaseUser?) {
        Log.d("AuthResult","${firebaseUser?.email}")
        withContext(Dispatchers.Main){
            progressBar.visibility = View.GONE
            Toast.makeText(this@GooglesignInActivity, "Success", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@GooglesignInActivity, BasicinfoActivity::class.java))
            finish()
        }
    }
    override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        if (currentUser!=null){
            startActivity(Intent(this@GooglesignInActivity, MainActivity::class.java))
            finish()
        }
    }
}


