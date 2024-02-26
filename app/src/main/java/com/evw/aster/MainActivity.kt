package com.evw.aster

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle

import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

lateinit var bottomNavigationView: BottomNavigationView
lateinit var relativelayout: RelativeLayout

lateinit var textview1:TextView
lateinit var textview2:TextView
    lateinit var mauth:FirebaseAuth
    lateinit var textview3:TextView
    val fragment1: Fragment = Michfragment()
    val fragment2: Fragment = friendListFragment()
    val fragment3: Fragment = NotificationFragment()
    lateinit var fm:FragmentManager
    var active = fragment1
    var uid = FirebaseAuth.getInstance().currentUser?.uid
    var prevalue = 0
    //     fm.beginTransaction().add(R.id.container_view, fragment3, "3").hide(fragment3).commit()
    //   fm.beginTransaction().add(R.id.container_view, fragment2, "2").hide(fragment2).commit()
    //   fm.beginTransaction().add(R.id.container_view,fragment1, "1").commit()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val token = intent.getStringExtra("fc")
        if (token != null){
            sendtokentoserver()
        }else{
          
        }
        askNotificationPermission()

        mauth = FirebaseAuth.getInstance()
        textview1 = findViewById(R.id.mich_text)
        textview2 = findViewById(R.id.friendList_text)
        textview3 = findViewById(R.id.notification_text)
        relativelayout = findViewById(R.id.lin)
        textview1.setTextColor(ContextCompat.getColor(this,R.color.Aster_neo))
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        fm = supportFragmentManager
        loadFragment(Michfragment())
        lifecycleScope.launch(Dispatchers.IO){
            getNotification().collect{
                withContext(Dispatchers.Main){
                    relativelayout.visibility = View.VISIBLE
                }

            }

        }

        lifecycleScope.launch(Dispatchers.IO){
            getmodeification().collect{
                if (it.type == DocumentChange.Type.MODIFIED){
                    withContext(Dispatchers.Main) {
                        relativelayout.visibility = View.GONE
                    }
                }
                if (it.type == DocumentChange.Type.REMOVED){
                    withContext(Dispatchers.Main) {
                        relativelayout.visibility = View.GONE
                    }
                }


            }

        }
        setonclicklistner()









        bottomNavigationView.setOnItemReselectedListener {
            when(it.itemId){
                R.id.mich ->{
                    return@setOnItemReselectedListener
                }
                R.id.friendlist ->{
                    addActivity()
                    return@setOnItemReselectedListener
                }

                R.id.notification ->{
                    loadFragment(NotificationFragment())
                    return@setOnItemReselectedListener
                }

            }
        }




    }

    private fun sendtokentoserver() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful){
                val tok = it.result
                val data = hashMapOf<String,Any>("fcm" to tok.toString())
                FirebaseFirestore.getInstance().collection("Users").document(uid.toString()).update(data).addOnSuccessListener {
                    FirebaseDatabase.getInstance().getReference("FCMTokens").child(uid.toString()).child("token").setValue(tok.toString())
                }
            }
        }


    }


    private fun setonclicklistner() {
        bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
              R.id.mich ->{
                  textview1.setTextColor(ContextCompat.getColor(this,R.color.Aster_neo))
                  textview2.setTextColor(ContextCompat.getColor(this,R.color.white))
                  textview3.setTextColor(ContextCompat.getColor(this,R.color.white))
                  loadFragment(Michfragment())
               //   fm.beginTransaction().hide(active).show(fragment1).commit()
                 // active = fragment1
                  true
              }

                R.id.friendlist ->{
                    addActivity()
                    true
                 /*   textview1.setTextColor(ContextCompat.getColor(this,R.color.white))
                    textview2.setTextColor(ContextCompat.getColor(this,R.color.Aster_neo))
                    textview3.setTextColor(ContextCompat.getColor(this,R.color.white))*/

                 //   fm.beginTransaction().hide(active).show(fragment2).commit()
                  //  active = fragment2

                }

                R.id.notification ->{
                    textview1.setTextColor(ContextCompat.getColor(this,R.color.white))
                    textview2.setTextColor(ContextCompat.getColor(this,R.color.white))
                    textview3.setTextColor(ContextCompat.getColor(this,R.color.Aster_neo))
                    loadFragment(NotificationFragment())
                  //  fm.beginTransaction().hide(active).show(fragment3).commit()
                  //  active = fragment3
                    true
                }

                else -> {
                    false
                }
            }
        }
    }

    private fun addActivity() {
    startActivity(Intent(applicationContext,ContactsyncActivity::class.java))



    }




    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {

        if (bottomNavigationView.selectedItemId != R.id.mich){
            bottomNavigationView.selectedItemId = R.id.mich
        } else{
          finish()
        }
    }




    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container_view,fragment)
        transaction.commit()
    }

    fun getNotification(): Flow<DocumentSnapshot> = callbackFlow {
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
                        trySend(document)

                    }

                }
            }
        }
        val registration = FirebaseFirestore.getInstance().collection("Notifications").document(uid.toString()).collection("notify").whereEqualTo("isread","false")
            .addSnapshotListener(listener)
        awaitClose { registration.remove() }
    }


    fun getmodeification(): Flow<DocumentChange> = callbackFlow {
        val listener = object : EventListener<QuerySnapshot> {
            override fun onEvent(snapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
                if (exception != null) {
                    // An error occurred
                    cancel()
                    return
                }
                if (snapshot != null) {

                    // The user document has data
                    for (document in snapshot.documentChanges){
                        trySend(document)

                    }

                }
            }
        }
        val registration = FirebaseFirestore.getInstance().collection("Notifications").document(uid.toString()).collection("notify")
            .addSnapshotListener(listener)
        awaitClose { registration.remove() }
    }


    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {

        }
    }








}



