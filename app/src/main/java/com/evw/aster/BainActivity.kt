package com.evw.aster

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.ripple.RippleUtils
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class BainActivity : AppCompatActivity() {
  lateinit var button: Button
  lateinit var textView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bain)
        button = findViewById(R.id.bnm)
        textView = findViewById(R.id.cvc)

        button.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                getUser().collect{
                    val bg = it?.password
                }
            }


        }
    }

    private val firestore = FirebaseFirestore.getInstance()

    fun getUser(): Flow<fgt?> = callbackFlow {
        val listener = object : EventListener<QuerySnapshot> {
            override fun onEvent(snapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
                if (exception != null) {
                    // An error occurred
                    cancel()
                    return
                }

                if (snapshot != null) {
                    // The user document has data
                   for (document in snapshot){
                      val user = document.toObject(fgt::class.java)
                       trySend(user)
                   }

                } else {
                    // The user document does not exist or has no data
                }
            }
        }
        val registration = firestore.collection("Usersname").whereEqualTo("phoneNumber","+916260657095").addSnapshotListener(listener)
        awaitClose { registration.remove() }
    }






}



