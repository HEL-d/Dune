package com.evw.aster

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class Astershop : Fragment() {

    lateinit var recylerview:RecyclerView
    lateinit var adapter:ShopAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_astershop, container, false)
         recylerview = view.findViewById(R.id.gridlayout)
         val layoutmanager:GridLayoutManager = GridLayoutManager(context,2)
         recylerview.layoutManager = layoutmanager
          adapter = ShopAdapter()
          recylerview.adapter = adapter





          lifecycleScope.launch(Dispatchers.IO){
              val flow = Pager(PagingConfig(1)) {
                  AsterPagingSource(FirebaseFirestore.getInstance())
              }.flow.collect {
                  withContext(Dispatchers.Main) {
                      adapter.submitData(it)
                  }
              }
          }







        return view
    }


    class AsterPagingSource(private val db: FirebaseFirestore) :
        PagingSource<QuerySnapshot, shopclass>(){

        override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, shopclass> {

            return try {
                val currentPage = params.key ?: db.collection("Astercollection").orderBy("timestamp", Query.Direction.DESCENDING).limit(8).get().await()
                val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]
                val nextPage = db.collection("Astercollection").orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(8).startAfter(lastDocumentSnapshot).get()
                    .await()
                LoadResult.Page(
                    data = currentPage.toObjects(shopclass::class.java),
                    prevKey = null,
                    nextKey = nextPage
                )
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }
        override fun getRefreshKey(state: PagingState<QuerySnapshot, shopclass>): QuerySnapshot? {
            return null
        }


    }











}