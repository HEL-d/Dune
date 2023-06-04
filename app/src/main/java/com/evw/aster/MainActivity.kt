package com.evw.aster
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
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
                    loadFragment(friendListFragment())
                    return@setOnItemReselectedListener
                }

                R.id.notification ->{
                    loadFragment(NotificationFragment())
                    return@setOnItemReselectedListener
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
                    textview1.setTextColor(ContextCompat.getColor(this,R.color.white))
                    textview2.setTextColor(ContextCompat.getColor(this,R.color.Aster_neo))
                    textview3.setTextColor(ContextCompat.getColor(this,R.color.white))
                    loadFragment(friendListFragment())
                 //   fm.beginTransaction().hide(active).show(fragment2).commit()
                  //  active = fragment2
                    true
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






}



