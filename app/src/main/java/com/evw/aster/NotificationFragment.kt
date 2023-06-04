package com.evw.aster
import android.app.Dialog
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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
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
            username = dbusername.toString()
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

            val data = hashMapOf("uid" to vid,"username" to message2,"name" to message3,"timestamp" to FieldValue.serverTimestamp(),"isblocked" to false)
            val dbc = FirebaseFirestore.getInstance().collection("FriendsList").document(uid.toString()).collection("friends")
                .document(vid).set(data).await()
              Buildfriendsnow(vid,message2)

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
        }*/


    }





    private suspend fun Buildfriendsnow(vid: String, message2: String):Boolean {
     //   val uid = FirebaseAuth.getInstance().currentUser?.uid
        return try {
            val data = hashMapOf("uid" to uid,"username" to username,"name" to name,"timestamp" to FieldValue.serverTimestamp(),"isblocked" to false)
            val dbc = FirebaseFirestore.getInstance().collection("FriendsList").document(vid).collection("friends")
                .document(uid.toString()).set(data).await()
                 Addfriends(vid,message2)
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

    private suspend fun Addfriends(vid: String, message2: String):Boolean {
     //   val uid = FirebaseAuth.getInstance().currentUser?.uid
        return try {
            val firstkey = uid?.substring(0,4)
              val secondkey = vid.substring(0,4)
              val mainkey = firstkey + secondkey
            val pushkey = FirebaseDatabase.getInstance().getReference("userRooms").child(uid.toString()).push().key
            val map = hashMapOf("Uroomname" to mainkey,"Allmember" to true,"lastmessage" to "say hii","timestamp" to ServerValue.TIMESTAMP.toString(),"isblocked" to false,"username" to message2,"roomid" to pushkey)
            val dbn = FirebaseDatabase.getInstance().getReference("userRooms").child(uid.toString()).child(pushkey.toString()).setValue(map).await()
               secondAdd(vid,pushkey)
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

    private suspend fun secondAdd(vid: String, pushkey: String?):Boolean {
     //   val uid = FirebaseAuth.getInstance().currentUser?.uid
        return try {
            val firstkey = uid?.substring(0,4)
            val secondkey = vid.substring(0,4)
            val mainkey = firstkey + secondkey
            val map = hashMapOf("Uroomname" to mainkey,"Allmember" to true,"lastmessage" to "say hii","timestamp" to ServerValue.TIMESTAMP.toString(),"isblocked" to false,"username" to username,"roomid" to pushkey)
            val db = FirebaseDatabase.getInstance().getReference("userRooms").child(vid).child(pushkey.toString()).setValue(map).await()
            sentkeytofirestore(vid,pushkey)
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

    private suspend fun sentkeytofirestore(vid: String, pushkey: String?):Boolean {
    //    val uid = FirebaseAuth.getInstance().currentUser?.uid
        return try {
            val data: HashMap<String, Any?> = hashMapOf("roomid" to pushkey,"Allmember" to true)
            val dbc = FirebaseFirestore.getInstance().collection("FriendsList").document(uid.toString()).collection("friends").document(vid).update(data).await()
              sentkeytofriend(vid,pushkey)
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

    private suspend fun sentkeytofriend(vid: String, pushkey: String?):Boolean {
   //     val uid = FirebaseAuth.getInstance().currentUser?.uid
        return try {
            val data:HashMap<String, Any?> = hashMapOf("roomid" to pushkey,"Allmember" to true)
            val dbc = FirebaseFirestore.getInstance().collection("FriendsList").document(vid).collection("friends").document(uid.toString()).update(data).await()
              finallyAccept(vid)
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

    private suspend fun finallyAccept(vid: String):Boolean {
      //  val uid = FirebaseAuth.getInstance().currentUser?.uid
        return try {
            val db = FirebaseFirestore.getInstance().collection("Notifications").document(uid.toString()).collection("notify").document(vid).update(mapOf("lptext" to "you","requestText" to "became friend with")).await()
            withContext(Dispatchers.Main){
                fragmentprogressdialog.dismiss()
                activity?.supportFragmentManager?.beginTransaction()?.detach(this@NotificationFragment)?.commit()
                activity?.supportFragmentManager?.beginTransaction()?.attach(this@NotificationFragment)?.commit()
            }
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









