package com.evw.aster
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.*
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.tasks.await



class NotificationFragment : Fragment(),NotificationAdapter.CallbackInterface {
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: NotificationAdapter
    lateinit var progressBar1: MaterialNeonProgressBar
    lateinit var progressBar2: MaterialNeonProgressBar
    lateinit var swipeRefreshlayout:SwipeRefreshLayout
    lateinit var networkbox: Networkbox
    lateinit var fragmentprogressdialog: Dialog
    var uid = FirebaseAuth.getInstance().currentUser?.uid
    lateinit var chip: Chip
    lateinit var fragmentviewdialog: fragmentviewdialog
    var username:String? = null
    var name:String? = null
    var profilepic:String? = null
    var senderroom:String? = null
    lateinit var internetConnectivity: InternetConnectivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_notification, container, false)
        adapter = NotificationAdapter(this)
        recyclerView = view.findViewById(R.id.first_recylerview)
        progressBar1 = view.findViewById(R.id.progressBar)
        progressBar2 = view.findViewById(R.id.progressBarLoadMore)
        chip = view.findViewById(R.id.nmo)
        swipeRefreshlayout = view.findViewById(R.id.swipe_lay)
        networkbox = Networkbox()
        fragmentviewdialog = fragmentviewdialog()
        internetConnectivity = InternetConnectivity()
        fragmentprogressdialog = Dialog(requireContext())
        initialsedialog()
        recyclerView.adapter = adapter
        lifecycleScope.launch(Dispatchers.IO) {
            val flow = Pager(PagingConfig(1)) {
                NotificationPagingSource(FirebaseFirestore.getInstance())
            }.flow.collect {
                withContext(Dispatchers.Main) {
                    adapter.submitData(it)
                }
            }
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest {
                progressBar1.isVisible = it.refresh is LoadState.Loading
                progressBar2.isVisible = it.append is LoadState.Loading
            }

        }

        lifecycleScope.launch(Dispatchers.IO){
            getUsername()
        }
        lifecycleScope.launch(Dispatchers.IO){
            getNamenow()
        }


        swipeRefreshlayout.setOnRefreshListener {
           activity?.supportFragmentManager?.beginTransaction()?.detach(this)?.commit()
            activity?.supportFragmentManager?.beginTransaction()?.attach(this)?.commit()
            swipeRefreshlayout.isRefreshing = false
        }

       lifecycleScope.launch(Dispatchers.IO){
            getNotification().collect{
                withContext(Dispatchers.Main){
                        chip.visibility = View.VISIBLE
                    }

            }


        }

       chip.setOnClickListener {
           lifecycleScope.launch(Dispatchers.IO){
              getNotification().collect{
                  val vid = it.id
                  setData(vid)
              }

           }

       }

       lifecycleScope.launch(Dispatchers.IO){
                  getmodeification().collect{
                   if (it.type == DocumentChange.Type.MODIFIED){
                       withContext(Dispatchers.Main) {
                           chip.visibility = View.GONE
                       }
                   }

                  }

       }




        return view
    }

    private fun initialsedialog() {
       fragmentprogressdialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        fragmentprogressdialog.setCancelable(false)
        fragmentprogressdialog.setContentView(R.layout.progressdialog)
    }

    private suspend fun getNamenow():DocumentSnapshot? {
        return try {
       //     val uid = FirebaseAuth.getInstance().currentUser?.uid
            val doc = FirebaseFirestore.getInstance().collection("Users").document(uid.toString()).get().await()
            val dbusername = doc.get("name")
            name = dbusername.toString()
            doc
        } catch (e:Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(context, "Nothing found", Toast.LENGTH_SHORT).show()
            }
            null
        }


    }

    private suspend fun getUsername():DocumentSnapshot? {
        return try {
            val doc = FirebaseFirestore.getInstance().collection("Users").document(uid.toString()).get().await()
            val dbusername = doc.get("username")
            val profilepics = doc.get("profilepic")
            username = dbusername.toString()
            if (profilepics == null){
                profilepic = "null"
            } else {
                profilepic = profilepics.toString()
            }
            doc
        } catch (e:Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(context, "Nothing found", Toast.LENGTH_SHORT).show()
            }
            null
        }
    }


    private suspend fun setData(vid: String) :Boolean{
        return try {
            val db = FirebaseFirestore.getInstance().collection("Notifications").document(uid.toString()).collection("notify")
                    .document(vid).update("isread","true").await()
            withContext(Dispatchers.Main){
                activity?.supportFragmentManager?.beginTransaction()?.detach(this@NotificationFragment)?.commit()
                activity?.supportFragmentManager?.beginTransaction()?.attach(this@NotificationFragment)?.commit()
            }
            true
        } catch (e:Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(context,"Failed to send",Toast.LENGTH_SHORT).show()
            }
            false
        }

    }


    class NotificationPagingSource(private val db: FirebaseFirestore) : PagingSource<QuerySnapshot, Notifyclass>() {
        override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Notifyclass> {
            return try {
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                val currentPage = params.key ?: db.collection("Notifications").document(uid.toString())
                    .collection("notify").orderBy("timestamp",Query.Direction.DESCENDING).limit(8).get().await()
                val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]
                val nextPage = db.collection("Notifications")
                    .document(uid.toString())
                    .collection("notify").orderBy("timestamp",Query.Direction.DESCENDING)
                    .limit(8).startAfter(lastDocumentSnapshot).get()
                    .await()

                LoadResult.Page(
                    data = currentPage.toObjects(Notifyclass::class.java),
                    prevKey = null,
                    nextKey = nextPage
                )
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }

        override fun getRefreshKey(state: PagingState<QuerySnapshot, Notifyclass>): QuerySnapshot? {
            return null
        }
    }

    override fun chip1message(message: String,message2:String,message3:String) {
        context?.let {
            internetConnectivity.checkInternetConnection(object:InternetConnectivity.ConnectivityCallback{
                override fun onDetected(isConnected: Boolean) {
                    if (isConnected){
                        fragmentprogressdialog.show()
                        CoroutineScope(Dispatchers.IO).launch {
                            firststep(message,message2,message3)
                        }
                    } else {
                       networkbox.shownetworkdialog(context)
                    }

                }

            }, it)
        }






    }

    override fun chip2message(message: String) {

        context?.let {
            internetConnectivity.checkInternetConnection(object:InternetConnectivity.ConnectivityCallback{
                override fun onDetected(isConnected: Boolean) {
                    if (isConnected){
                        CoroutineScope(Dispatchers.IO).launch {
                            deleteRequest(message)
                        }
                    } else {
                        networkbox.shownetworkdialog(context)
                    }

                }

            }, it)
        }






    }

    override fun imagviewmessage(name: String, uid: String, username: String) {
        val intent: Intent = Intent(context,publicprofileactivity2::class.java)
        intent.putExtra("username",username)
        intent.putExtra("uid",uid)
        intent.putExtra("name",name)
        startActivity(intent)
    }


    private suspend fun deleteRequest(message: String):Boolean {
     //   val uid = FirebaseAuth.getInstance().currentUser?.uid
        return try {
            val db = FirebaseFirestore.getInstance().collection("Notifications").document(uid.toString()).collection("notify")
                .document(message).delete().await()
            withContext(Dispatchers.Main){
                activity?.supportFragmentManager?.beginTransaction()?.detach(this@NotificationFragment)?.commit()
                activity?.supportFragmentManager?.beginTransaction()?.attach(this@NotificationFragment)?.commit()
            }
            true
        } catch (e:Exception){
            withContext(Dispatchers.Main){
                fragmentviewdialog.showDialog(context,"something went wrong Try again later","Check your network connection and then proceed")
                Toast.makeText(context,"Failed to send", Toast.LENGTH_SHORT).show()
            }
            false
        }











     /*   return try {

         *//*    val uid = FirebaseAuth.getInstance().currentUser?.uid
            val doc = FirebaseFirestore.getInstance().collection("Notifications").document(uid.toString()).collection("notify")
                .whereEqualTo("uid",message).get().await()

            if (!doc.isEmpty){
                for (data in doc){
                    deltefinally(message)
                }
            }*//*
            doc
        } catch (e:Exception){
            withContext(Dispatchers.Main){
               fragmentviewdialog.showDialog(context,"something went wrong Try again later","Check your network connection and then proceed")
                Toast.makeText(context, "Nothinf found", Toast.LENGTH_SHORT).show()
            }
            null
        }*/
    }




    private suspend fun firststep(vid: String, message2: String, message3: String): Boolean {
       // val uid = FirebaseAuth.getInstance().currentUser?.uid
        return try {
            val roomname = uid?.take(3) + vid.take(3)
            val data = hashMapOf("uid" to vid,"username" to message2,"name" to message3,"timestamp" to FieldValue.serverTimestamp(),"isblocked" to false,"roomname" to roomname)
            val dbc = FirebaseFirestore.getInstance().collection("FriendsList").document(uid.toString()).collection("friends")
                .document(vid).set(data).await()
              Buildfriendsnow(vid,message2,roomname)

            true
        } catch (e:Exception){
            withContext(Dispatchers.Main){
                fragmentprogressdialog.dismiss()
                fragmentviewdialog.showDialog(context,"something went wrong Try again later","Check your network connection and then proceed")
                Toast.makeText(context,"Failed to send", Toast.LENGTH_SHORT).show()
            }
            false
        }








      /*  return try {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val doc = FirebaseFirestore.getInstance().collection("Notifications").document(uid.toString()).collection("notify")
                .whereEqualTo("uid",vid).get().await()
            if (!doc.isEmpty){
                for (data in doc){
                    val tid = data.id
                    acceptrequest(tid,vid,message2,message3)
                }
            }
            doc
        } catch (e:Exception){
            withContext(Dispatchers.Main){
                fragmentviewdialog.showDialog(context,"something went wrong Try again later","Check your network connection and then proceed")
                Toast.makeText(context, "Nothinf found", Toast.LENGTH_SHORT).show()
            }
            null



                          finallyAccept(vid)











        }*/


    }





    private suspend fun Buildfriendsnow(vid: String, message2: String, roomname: String):Boolean {
     //   val uid = FirebaseAuth.getInstance().currentUser?.uid
        return try {
            val data = hashMapOf("uid" to uid,"username" to username,"name" to name,"timestamp" to FieldValue.serverTimestamp(),"isblocked" to false,"roomname" to roomname)
            val dbc = FirebaseFirestore.getInstance().collection("FriendsList").document(vid).collection("friends")
                .document(uid.toString()).set(data).await()
                 Checkfirstfriendship(vid)


          //  Addfriends(vid,message2)
            true
        } catch (e:Exception){
            withContext(Dispatchers.Main){
                fragmentprogressdialog.dismiss()
                fragmentviewdialog.showDialog(context,"something went wrong Try again later","Check your network connection and then proceed")
                Toast.makeText(context,"Failed to send", Toast.LENGTH_SHORT).show()
            }
            false
        }
    }


    private suspend fun Checkfirstfriendship(vid: String): DataSnapshot? {
        return try {
            val sender = vid + uid
            val doc = FirebaseDatabase.getInstance().getReference("Message").child(sender).get().await()
            if(doc.exists()){
               finallyAccept(vid)
            } else {
                val negativetime1 = -1699599203757
                val negativetime2 = -1699600353986
                val p1 = "Do not use abusive language during mich, we don't support any type of vulgarity in app, you can inform us if you faces any problem during mich , our customer support is 24 x 7"
                val p2 = "Use emojis from chatbox to animate the avatar, Animated avatar can make  more real and fun chat between user, we are adding  more emojis in upcoming updates "
                val p3 = "Do not use abusive language during mich, we don't support any type of vulgarity in app, you can inform us if you faces any problem during mich , our customer support is 24 x 7"
                val p4 = "this side is used to spawn your partner avatar, and other side for your own avatar, you can inform us if you faces any problem during mich, more features are coming soon"
                val vp = MessageClass(uid,p1,negativetime1)
                val sp = MessageClass(uid,p2,negativetime2)
                val tp = MessageClass(uid,p3,negativetime1)
                val bp = MessageClass(uid,p4,negativetime2)
                val sender1 = vid + uid
                val reciver = uid + vid
                FirebaseDatabase.getInstance().getReference("Message").child(sender1).push().setValue(vp).await()
                FirebaseDatabase.getInstance().getReference("Message").child(sender1).push().setValue(sp).await()
                FirebaseDatabase.getInstance().getReference("Message2").child(reciver).push().setValue(tp).await()
                FirebaseDatabase.getInstance().getReference("Message2").child(reciver).push().setValue(bp).await()
                FirebaseDatabase.getInstance().getReference("Message").child(reciver).push().setValue(vp).await()
                FirebaseDatabase.getInstance().getReference("Message").child(reciver).push().setValue(sp).await()
                FirebaseDatabase.getInstance().getReference("Message2").child(sender).push().setValue(tp).await()
                FirebaseDatabase.getInstance().getReference("Message2").child(sender).push().setValue(bp).await()
                finallyAccept(vid)
            }
            doc
        } catch (e:Exception){
            withContext(Dispatchers.Main){

            }
            null
        }



    }











    private suspend fun finallyAccept(vid: String):Boolean {
      //  val uid = FirebaseAuth.getInstance().currentUser?.uid
        return try {
            val db = FirebaseFirestore.getInstance().collection("Notifications").document(uid.toString()).collection("notify").document(vid).update(mapOf("lptext" to "you","requestText" to "became friend with")).await()
            sendNotificationstoother(vid)
            true
        } catch (e:Exception){
            withContext(Dispatchers.Main){
                fragmentprogressdialog.dismiss()
                fragmentviewdialog.showDialog(context,"something went wrong Try again later","Check your network connection and then proceed")
                Toast.makeText(context,"Failed to send", Toast.LENGTH_SHORT).show()
            }
            false
        }
    }

    private suspend fun sendNotificationstoother(vid: String):Boolean {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        return try {
            val data = hashMapOf("username" to username, "uid" to uid, "lptext" to "you","name" to name,"requestText"
                        to "became friend with","timestamp" to FieldValue.serverTimestamp(),"isread" to "false","frid" to vid)
                val db = FirebaseFirestore.getInstance().collection("Notifications").document(vid).collection("notify")
                    .document(uid.toString()).set(data).await()
            withContext(Dispatchers.Main){
                fragmentprogressdialog.dismiss()
                activity?.supportFragmentManager?.beginTransaction()?.detach(this@NotificationFragment)?.commit()
                activity?.supportFragmentManager?.beginTransaction()?.attach(this@NotificationFragment)?.commit()
            }

            true

        } catch (e:Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(context,"Failed to send",Toast.LENGTH_SHORT).show()
            }
            false
        }


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









