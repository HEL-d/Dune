package com.evw.aster
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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


class SceneActivity : AppCompatActivity() {
    lateinit var recyclerView2: RecyclerView
    lateinit var adapter2: Goadapter
    lateinit var progressBar1: MaterialNeonProgressBar
    lateinit var progressBar2: MaterialNeonProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scene)
        adapter2 = Goadapter()
        recyclerView2 = findViewById(R.id.ryc)
        progressBar1 = findViewById(R.id.progressBar)
        progressBar2 = findViewById(R.id.progressBarLoadMore)
        recyclerView2.adapter = adapter2
        lifecycleScope.launch(Dispatchers.IO){
            val flow = Pager(PagingConfig(1)) {
                qwePagingSource(FirebaseFirestore.getInstance())
            }.flow.collect{
                withContext(Dispatchers.Main){
                    adapter2.submitData(it)

                }

            }
        }


        lifecycleScope.launch {
            adapter2.loadStateFlow.collectLatest {
                progressBar1.isVisible = it.refresh is LoadState.Loading
                progressBar2.isVisible = it.append is LoadState.Loading
            }
        }

        }

    class qwePagingSource(private val db: FirebaseFirestore) : PagingSource<QuerySnapshot, fat>() {
        override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, fat> {
            return try {
                val currentPage = params.key ?: db.collection("Usersname").limit(8).get().await()
                val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]
                val nextPage = db.collection("Usersname")
                    .limit(8).startAfter(lastDocumentSnapshot).get()
                    .await()

                LoadResult.Page(
                    data = currentPage.toObjects(fat::class.java),
                    prevKey = null,
                    nextKey = nextPage
                )
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }

        override fun getRefreshKey(state: PagingState<QuerySnapshot, fat>): QuerySnapshot? {
            return null
        }
    }

}

