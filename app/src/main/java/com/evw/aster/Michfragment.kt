package com.evw.aster


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class Michfragment : Fragment() {
  //  lateinit var relativeLayout: RelativeLayout
//    lateinit var relativeLayout1: RelativeLayout
   lateinit var recyclerView: RecyclerView
    lateinit var adapter: MichAdapternow
    lateinit var textView: TextView
    lateinit var lm:LinearLayoutManager
    lateinit var chip1:Chip
    lateinit var chip2:Chip
    lateinit var materialNeonProgressBar: MaterialNeonProgressBar
 //   lateinit var list:ArrayList<run>
    var uid = FirebaseAuth.getInstance().currentUser?.uid
    var last_key = ""
    var last_node:String? = ""
    var isMaxData = false
    var isScrolling:Boolean = false
    var ITEM_LOAD_COUNT = 8
    var currentitems = 0
    var tottalitems:kotlin.Int = 0
    var scrolledoutitems:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_michfragment, container, false)
//        relativeLayout = view.findViewById(R.id.baxz)
//        relativeLayout1 = view.findViewById(R.id.rel)
        textView = view.findViewById(R.id.vvv)
        recyclerView = view.findViewById(R.id.recyclerView_mich)
         chip1 = view.findViewById(R.id.nmo)
         chip2 = view.findViewById(R.id.nmo2)
        lm = LinearLayoutManager(context)
        materialNeonProgressBar = view.findViewById(R.id.progressBarLoadMore)
        //  list = arrayListOf()
        lifecycleScope.launch(Dispatchers.IO){
            getKeyFromFirebase().collectLatest {
                for (lastkey in it.children){
                    last_key = lastkey.key.toString()
                }

            }
        }
        recyclerView.layoutManager = lm
        adapter = MichAdapternow()
        recyclerView.adapter = adapter
        FetchData()
       /* lifecycleScope.launch(Dispatchers.IO){
            val flow = Pager(PagingConfig(1)) {
               ChatPagingSorce(FirebaseDatabase.getInstance())
            }.flow.collect {
                withContext(Dispatchers.Main) {
                    adapter.submitData(it)

                }
            }

        }*/

       /*  lifecycleScope.launch {
             adapter.loadStateFlow.collectLatest {
                 materialNeonProgressBar.isVisible = it.append is LoadState.Loading
             }
         }*/






      /*  relativeLayout.setOnClickListener {
            context?.startActivity(Intent(context, SearchActivity::class.java))
        }
        relativeLayout1.setOnClickListener {
            context?.startActivity(Intent(context, ContactsyncActivity::class.java))
        }*/
        chip1.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                if (!isMaxData) {
                    getData().collectLatest {
                        withContext(Dispatchers.Main){
                            chip1.visibility = View.GONE
                        }


                        if (it.hasChildren()) {
                            val list: ArrayList<run> = arrayListOf()
                            for (usersnapshot in it.children) {
                                val bc = usersnapshot.getValue(run::class.java)
                                list.add(bc!!)
                            }
                            last_node = list[list.size - 1].timestamp
                            withContext(Dispatchers.Main) {
                                adapter.setnewData(list)
                                recyclerView.scrollToPosition(0)
                            }


                        } else {
                            isMaxData = true
                        }




                    }
                } else {
                    //do noting
                }
            }

        }
        chip2.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                if (!isMaxData) {
                    FetchDataNew().collectLatest {
                        withContext(Dispatchers.Main) {
                            chip2.visibility = View.GONE
                            chip1.visibility = View.VISIBLE
                        }
                        if (it.hasChildren()) {
                            val list: ArrayList<run> = arrayListOf()
                            for (usersnapshot in it.children) {
                             val bc = usersnapshot.getValue(run::class.java)
                                list.add(bc!!)
                            }

                            last_node = list.get(list.size - 1).timestamp
                            withContext(Dispatchers.Main) {
                                adapter.setnewData(list)

                            }



                            /*  if (!last_node.equals(last_key))
                               list.removeAt(list.size - 1)
                           else
                               last_node = "end"*/


                        } else {
                            isMaxData = true
                        }

                    }
                } else {
                    //do nothing
                }
            }

        }




       recyclerView.addOnScrollListener(object:RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling = true
                }

            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                currentitems= lm.childCount
                tottalitems= lm.itemCount
                scrolledoutitems= lm.findFirstVisibleItemPosition()
                if (isScrolling && currentitems + scrolledoutitems == tottalitems){
                    isScrolling = false
                    chip2.visibility = View.VISIBLE
                }



            }
        })


      




        return view
    }




    private fun getKeyFromFirebase():Flow<DataSnapshot> = callbackFlow {
        val listener = object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot)

            }

            override fun onCancelled(error: DatabaseError) {
                cancel()
                return
            }

        }
        val getLastkey:Query = FirebaseDatabase.getInstance().getReference("userRooms").child(uid.toString()).orderByKey().limitToLast(1)
        getLastkey.addListenerForSingleValueEvent(listener)
        awaitClose { getLastkey.removeEventListener(listener) }
    }

    private  fun FetchData() {
        lifecycleScope.launch(Dispatchers.IO) {
            if (!isMaxData) {
                getData().collectLatest {
                    if (it.hasChildren()) {
                        val list: ArrayList<run> = arrayListOf()
                        for (usersnapshot in it.children) {
                            val bc = usersnapshot.getValue(run::class.java)
                             list.add(bc!!)
                        }
                        last_node = list[list.size - 1].timestamp
                        withContext(Dispatchers.Main) {
                            adapter.setnewData(list)

                        }


                    } else {
                        isMaxData = true
                    }




                }
            } else {
                //do noting
            }
        }




    }

    private fun getData() :Flow<DataSnapshot> = callbackFlow {

        val listener = object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                 trySend(snapshot)

            }

            override fun onCancelled(error: DatabaseError) {
               cancel()
                return
            }

        }
        val query:Query = FirebaseDatabase.getInstance().getReference("userRooms").child(uid.toString()).orderByChild("timestamp").limitToFirst(9)
        query.addValueEventListener(listener)
        awaitClose { query.removeEventListener(listener) }
    }

    private fun FetchDataNew(): Flow<DataSnapshot> = callbackFlow {

        val listener = object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot)

            }

            override fun onCancelled(error: DatabaseError) {
                cancel()
                return
            }


        }
        val query:Query = FirebaseDatabase.getInstance().getReference("userRooms").child(uid.toString()).orderByChild("timestamp").startAt(last_node).limitToFirst(9)
            query.addValueEventListener(listener)
            awaitClose { query.removeEventListener(listener) }
    }






}





   class ChatPagingSorce(private val db: FirebaseDatabase) : PagingSource<DataSnapshot, run>() {
        override suspend fun load(params: LoadParams<DataSnapshot>): LoadResult<DataSnapshot, run> {
            return try {
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                val currentPage =
                    params.key ?: db.getReference("userRooms").child(uid.toString()).orderByKey()
                        .limitToFirst(8).get().await()
                val lastDocumentSnapshot = currentPage.children.last().key
                val nextPage = db.getReference("userRooms").child(uid.toString()).orderByKey()
                    .limitToFirst(8).startAfter(lastDocumentSnapshot).get().await()
                val products = currentPage.children.map {
                    it.getValue(run::class.java)!!
                }

                LoadResult.Page(
                    data = products,
                    prevKey = null,
                    nextKey = nextPage
                )

            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }


        override fun getRefreshKey(state: PagingState<DataSnapshot, run>): DataSnapshot? {
            return null
        }


    }
















