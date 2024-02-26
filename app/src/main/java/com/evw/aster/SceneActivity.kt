package com.evw.aster
import android.os.Bundle
import android.view.MotionEvent
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await


class SceneActivity : AppCompatActivity() {
    lateinit var linearLayout: LinearLayout
   /* lateinit var recyclerView2: RecyclerView
    lateinit var adapter2: Goadapter
    lateinit var progressBar1: MaterialNeonProgressBar
    lateinit var progressBar2: MaterialNeonProgressBar*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scene)
       linearLayout = findViewById(R.id.lan)
















     /*   adapter2 = Goadapter()
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
        }*/

        }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        val pointerId = event.getPointerId(0)
        val pointerIndex = event.findPointerIndex(pointerId)
        // Get the pointer's current position
        val x = event.getX(pointerIndex)
        val y = event.getY(pointerIndex)
        println("X:$x")
        println("Y:$y")
        return true
    }

    class qwePagingSource(private val db: FirebaseFirestore) : PagingSource<QuerySnapshot, Searchavatarclass>() {
        override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Searchavatarclass> {
            return try {
                val currentPage = params.key ?: db.collection("Usersname").limit(8).get().await()
                val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]
                val nextPage = db.collection("Usersname")
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

}

