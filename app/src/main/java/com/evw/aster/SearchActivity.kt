package com.evw.aster

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class SearchActivity : AppCompatActivity(),Goadapter.SearchInerface {
    lateinit var searchView: SearchView
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: Goadapter
    lateinit var relativeLayout: RelativeLayout
    lateinit var progressBar1: MaterialNeonProgressBar
    lateinit var progressBar2: MaterialNeonProgressBar
    lateinit var userList: ArrayList<Searchavatarclass>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        searchView = findViewById(R.id.searchViewnow)
        adapter = Goadapter(this)
        recyclerView = findViewById(R.id.first_recylerview)
        progressBar1 = findViewById(R.id.progressBar)
        progressBar2 = findViewById(R.id.progressBarLoadMore)
        relativeLayout = findViewById(R.id.gfg)
        userList = arrayListOf()
        searchView.setOnSearchClickListener {
            searchView.onActionViewExpanded()
            relativeLayout.isActivated = true
        }
        searchView.setOnClickListener {
            relativeLayout.isActivated = true
            searchView.onActionViewExpanded()
        }

        searchView.setOnCloseListener {
            relativeLayout.isActivated = false
            false
        }



        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                lifecycleScope.launch(Dispatchers.IO) {
                    if (query != null){
                        if (query.isNotEmpty()){
                            val flow = Pager(PagingConfig(1)) {
                                FirestorePagingSource(FirebaseFirestore.getInstance(),query)
                            }.flow.collect{
                                withContext(Dispatchers.Main){
                                    adapter.submitData(it)
                                    recyclerView.adapter = adapter
                                }
                            }
                        } else {
                            withContext(Dispatchers.Main){
                                progressBar1.visibility = View.GONE
                                adapter.submitData(PagingData.empty())
                                recyclerView.adapter = adapter
                            }
                        }
                    }


                }

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
             lifecycleScope.launch(Dispatchers.IO) {
                 if (newText != null){
                     if (newText.isNotEmpty()){
                         val flow = Pager(PagingConfig(1)) {
                             FirestorePagingSource(FirebaseFirestore.getInstance(),newText)
                         }.flow.collect{
                             withContext(Dispatchers.Main){
                                 adapter.submitData(it)
                                 recyclerView.adapter = adapter
                             }

                         }
                     } else {
                         withContext(Dispatchers.Main){
                             progressBar1.visibility = View.GONE
                             adapter.submitData(PagingData.empty())
                             recyclerView.adapter = adapter
                         }


                     }
                 }


             }





                return false
            }

        })

        lifecycleScope.launch {
        adapter.loadStateFlow.collectLatest {
            progressBar1.isVisible = it.refresh is LoadState.Loading
            progressBar2.isVisible = it.append is LoadState.Loading
        }


        }


    }


    class FirestorePagingSource(private val db: FirebaseFirestore, private val newText:String) : PagingSource<QuerySnapshot, Searchavatarclass>() {
        override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Searchavatarclass> {
            return try {
                val currentPage = params.key ?: db.collection("Usersname")
                    .whereGreaterThanOrEqualTo("username", newText)
                    .whereLessThanOrEqualTo("username", newText + '\uf8ff').limit(8).get().await()
                val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]
                val nextPage = db.collection("Usersname")
                    .whereGreaterThanOrEqualTo("username", newText)
                    .whereLessThanOrEqualTo("username", newText + '\uf8ff')
                    .limit(8).startAfter(lastDocumentSnapshot).get()
                    .await()

                LoadResult.Page(
                    data = currentPage.toObjects(Searchavatarclass::class.java),
                    prevKey = null,
                    nextKey = nextPage
                )
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }

        override fun getRefreshKey(state: PagingState<QuerySnapshot, Searchavatarclass>): QuerySnapshot? {
            return null
        }
    }

    override fun sendtonew(username: String, uid: String, profilepic: String, avatarurl: String) {
        val intent: Intent = Intent(this@SearchActivity,publicprofileactivity::class.java)
        intent.putExtra("username",username)
        intent.putExtra("profilepic",profilepic)
        intent.putExtra("uid",uid)
        intent.putExtra("avatarurl",avatarurl)
        startActivity(intent)


    }

}

    /**   private fun searchusers(newText: String?) {
        if (newText != null) {
            if (newText.isNotEmpty()) {
                val query = FirebaseFirestore.getInstance().collection("Usersname")
                    .whereGreaterThanOrEqualTo("username", newText)
                    .whereLessThanOrEqualTo("username", newText + '\uf8ff').limit(limit.toLong())
                query.addSnapshotListener { value, error ->
                    if (value != null) {
                        userList.clear()
                        for (data in value.documents) {
                            val user: fat? = data.toObject(fat::class.java)
                            if (user != null) {
                                userList.add(user)
                            }
                        }
                        adapter.submitList(userList)
                        recyclerView.adapter = adapter
                    }

                }
            }else {
                userList.clear()
                adapter.submitList(userList)
                recyclerView.adapter = adapter
            }

        }

    }   **/









    /*   private fun searchUsers(recherche: String?) {
           if (recherche != null) {
               if (recherche.isNotEmpty()) {
                   FirebaseFirestore.getInstance().collection("Usersname")
                       .whereGreaterThanOrEqualTo("username", recherche)
                       .whereLessThanOrEqualTo("username", recherche + '\uf8ff')
                       .addSnapshotListener { value, error ->
                           if (value != null) {
                               userList.clear()
                               for (data in value.documents) {
                                   val user: fat? = data.toObject(fat::class.java)
                                   if (user != null) {
                                       userList.add(user)
                                   }
                               }
                               adapter.submitList(userList)
                               recyclerView.adapter = adapter
                           }


                       }

               } else {
                   userList.clear()
                   adapter.submitList(userList)
                   recyclerView.adapter = adapter
               }


           }

       }   */















/**  CoroutineScope(Dispatchers.IO).launch {
getUsersname(lastVisibleItem,query).collect{
for (data in it){
val po: fgt? = data.toObject(fgt::class.java)
if (po != null) {
userList.add(po)
}
}
withContext(Dispatchers.Main){
adapter.submitList(userList)
recyclerView.adapter = adapter
}
}
}     **/





/**    private fun getUsersname(lastVisibleItem: MutableStateFlow<Int>, query: String?): Flow<List<DocumentSnapshot>> = flow {
if (query != null) {
if (query.isNotEmpty()) {
val users = mutableListOf<DocumentSnapshot>()
users.addAll( suspendCoroutine {
FirebaseFirestore.getInstance().collection("Usersname")
.whereGreaterThanOrEqualTo("username", query)
.whereLessThanOrEqualTo("username", query + '\uf8ff')
.limit(15).get().addOnSuccessListener { querysnapshot ->
userList.clear()
it.resume(querysnapshot.documents)

}
})
emit(users)
lastVisibleItem.transform { lastVisibleItem ->
if (lastVisibleItem == users.size && users.size > 0) {
users.addAll(suspendCancellableCoroutine{
FirebaseFirestore.getInstance().collection("Usersname")
.whereGreaterThanOrEqualTo("password", query)
.whereLessThanOrEqualTo("password", query + '\uf8ff')
.startAfter(users.last()).limit(15).get()
.addOnSuccessListener { querysnapshot ->
userList.clear()
it.resume(querysnapshot.documents)
}
})
emit(users)
}
}

} else {
userList.clear()
withContext(Dispatchers.Main){
adapter.submitList(userList)
recyclerView.adapter = adapter
}



}
}

}     **/

//  searchUsers(newText)
/*     CoroutineScope(Dispatchers.IO).launch {
         getUsersname(lastVisibleItem,newText).collect{
             for (data in it){
                 val po: fgt? = data.toObject(fgt::class.java)
                 if (po != null) {
                     userList.add(po)
                 }
             }
             withContext(Dispatchers.Main){
                 adapter.submitList(userList)
                 recyclerView.adapter = adapter
             }
         }
     }  **/









