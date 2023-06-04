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
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.tasks.await


class friendListFragment : Fragment(),FriendListAdapter.MInterface {
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: FriendListAdapter
    lateinit var progressBar1: MaterialNeonProgressBar
    lateinit var progressBar2: MaterialNeonProgressBar
    lateinit var swipeRefreshlayout: SwipeRefreshLayout
    var uid = FirebaseAuth.getInstance().currentUser?.uid
    lateinit var fragmentviewdialog: fragmentviewdialog
    lateinit var floatingActionButton: ExtendedFloatingActionButton
    lateinit var internetConnectivity: InternetConnectivity
    lateinit var networkbox: Networkbox
    lateinit var fragmentprogressdialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view:View = inflater.inflate(R.layout.fragment_friend_list, container, false)
        floatingActionButton = view.findViewById(R.id.cvc)
        adapter = FriendListAdapter(this)
        recyclerView = view.findViewById(R.id.first_recylerview)
        progressBar1 = view.findViewById(R.id.progressBar)
        progressBar2 = view.findViewById(R.id.progressBarLoadMore)
        swipeRefreshlayout = view.findViewById(R.id.swipe_lay)
        fragmentviewdialog = fragmentviewdialog()
        fragmentprogressdialog = Dialog(requireContext())
        initialsedialog()
        internetConnectivity = InternetConnectivity()
        networkbox = Networkbox()
        recyclerView.adapter = adapter
        lifecycleScope.launch(Dispatchers.IO) {
            val flow = Pager(PagingConfig(1)) {
                FriendPaginSource(FirebaseFirestore.getInstance())
            }.flow.collect {
                withContext(Dispatchers.Main) {
                    adapter.submitData(it)
                }
            }
        }
        floatingActionButton.setOnClickListener {
            context?.startActivity(Intent(context,ContactsyncActivity::class.java))
        }

        swipeRefreshlayout.setOnRefreshListener {
            activity?.supportFragmentManager?.beginTransaction()?.detach(this)?.commit()
            activity?.supportFragmentManager?.beginTransaction()?.attach(this)?.commit()
            swipeRefreshlayout.isRefreshing = false
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest {
                progressBar1.isVisible = it.refresh is LoadState.Loading
                progressBar2.isVisible = it.append is LoadState.Loading
            }

        }



        return view
    }

    private fun initialsedialog() {
        fragmentprogressdialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        fragmentprogressdialog.setCancelable(false)
        fragmentprogressdialog.setContentView(R.layout.progressdialog)
    }

    class FriendPaginSource(private val db: FirebaseFirestore) : PagingSource<QuerySnapshot, FriendListClass>() {
        override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, FriendListClass> {
            return try {
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                val currentPage = params.key ?: db.collection("FriendsList").document(uid.toString())
                    .collection("friends").orderBy("timestamp", Query.Direction.DESCENDING).limit(8).get().await()
                val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]
                val nextPage = db.collection("FriendsList")
                    .document(uid.toString())
                    .collection("friends").orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(8).startAfter(lastDocumentSnapshot).get()
                    .await()

                LoadResult.Page(
                    data = currentPage.toObjects(FriendListClass::class.java),
                    prevKey = null,
                    nextKey = nextPage
                )
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }

        override fun getRefreshKey(state: PagingState<QuerySnapshot, FriendListClass>): QuerySnapshot? {
            return null
        }
    }







    override fun chip1message(uid: String, roomid: String) {
        context?.let {
            internetConnectivity.checkInternetConnection(object:InternetConnectivity.ConnectivityCallback{
                override fun onDetected(isConnected: Boolean) {
                    if (isConnected){
                        fragmentprogressdialog.show()
                       lifecycleScope.launch(Dispatchers.IO){
                           deletefriend(uid,roomid)
                       }


                    } else {
                       networkbox.shownetworkdialog(context)
                    }

                }

            }, it)
        }







    }

    private suspend fun deletefriend(vid: String, roomid: String):Boolean {
        return try {
            val db = FirebaseFirestore.getInstance().collection("FriendsList").document(uid.toString()).collection("friends")
                .document(vid).delete().await()
            deleteAllmember(vid,roomid)
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

    private suspend fun deleteAllmember(vid: String, roomid: String):Boolean {
        return try {
            val db = FirebaseFirestore.getInstance().collection("FriendsList").document(vid).collection("friends").document(uid.toString()).delete().await()
               checkforfrontchats(vid,roomid).collect{
                   removefrontchtas(vid, roomid)
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

    private fun checkforfrontchats(vid: String, roomid: String):Flow<DataSnapshot> = callbackFlow {
        val listener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    trySend(snapshot)
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        setmembertofalse(vid, roomid).collect{
                            finallyset(vid, roomid)
                        }
                    }
                }

            }
            override fun onCancelled(error: DatabaseError) {
                cancel()
                return
            }
        }
        val registration = FirebaseDatabase.getInstance().getReference("userRooms").child(uid.toString()).child(roomid)
        registration.addValueEventListener(listener)
        awaitClose { registration.removeEventListener(listener)}
    }


   /* private fun checkchatroom(vid: String, roomid: String):Flow<DataSnapshot> = callbackFlow {
        val listener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    trySend(snapshot)

                } else {
                   CoroutineScope(Dispatchers.IO).launch {
                       removefrontchtas(vid, roomid)
                   }


                }

            }
            override fun onCancelled(error: DatabaseError) {
                cancel()
                return
            }
        }

        val registration = FirebaseDatabase.getInstance().getReference("chats").child(roomid).child(uid.toString())
        registration.addValueEventListener(listener)
        awaitClose { registration.removeEventListener(listener)}
    }*/


   /* private suspend fun deletechatroom(vid: String, roomid: String):Boolean {
        return try {
            val db = FirebaseDatabase.getInstance().getReference("chats").child(roomid).child(uid.toString())
                .removeValue().await()
                removefrontchtas(vid,roomid)
            true
        } catch (e:Exception){
            withContext(Dispatchers.Main){
                fragmentviewdialog.showDialog(context,"something went wrong Try again later","Check your network connection and then proceed")
                Toast.makeText(context,"Failed to send", Toast.LENGTH_SHORT).show()
            }
            false
        }

    }*/

    private suspend fun removefrontchtas(vid: String, roomid: String):Boolean {
        return try {
            val dbc = FirebaseDatabase.getInstance().getReference("userRooms").child(uid.toString()).child(roomid)
                .child("Allmember").setValue(false).await()
               setmembertofalse(vid, roomid).collect{
                   finallyset(vid, roomid)
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






    private  fun setmembertofalse(vid: String, roomid: String):Flow<DataSnapshot> = callbackFlow {
        val listener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    trySend(snapshot)
                } else {
                 CoroutineScope(Dispatchers.Main).launch {
                     fragmentprogressdialog.dismiss()
                 }
                }

            }
            override fun onCancelled(error: DatabaseError) {
                 cancel()
                 return
            }
        }

        val registration = FirebaseDatabase.getInstance().getReference("userRooms").child(vid).child(roomid)
         registration.addValueEventListener(listener)
        awaitClose { registration.removeEventListener(listener)}
    }



    private suspend fun finallyset(vid: String, roomid: String):Boolean {
        return try {
            val dbc = FirebaseDatabase.getInstance().getReference("userRooms").child(vid).child(roomid)
                    .child("Allmember").setValue(false).await()
            withContext(Dispatchers.Main){
                fragmentprogressdialog.dismiss()
                activity?.supportFragmentManager?.beginTransaction()?.detach(this@friendListFragment)?.commit()
                activity?.supportFragmentManager?.beginTransaction()?.attach(this@friendListFragment)?.commit()
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

/*    private suspend fun setUsernametofalse(vid: String, roomid: String):Boolean {
        return try {
            val dbc = FirebaseDatabase.getInstance().getReference("userRooms").child(vid).child(roomid)
                .child("username").setValue("Aster account").await()
             nowsetusername(vid,roomid)
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

    private suspend fun nowsetusername(vid: String, roomid: String):Boolean {
        return try {
            val dbc = FirebaseDatabase.getInstance().getReference("userRooms").child(uid.toString()).child(roomid)
                .child("username").setValue("Aster account").await()
            withContext(Dispatchers.Main){
                fragmentprogressdialog.dismiss()
                activity?.supportFragmentManager?.beginTransaction()?.detach(this@friendListFragment)?.commit()
                activity?.supportFragmentManager?.beginTransaction()?.attach(this@friendListFragment)?.commit()
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

    }*/


}