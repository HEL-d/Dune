package com.evw.aster

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await


class Allfun {}
//class LoginActivity : AppCompatActivity() {
//    lateinit var editText1: EditText
//    lateinit var editText2: EditText
//    lateinit var editText3:EditText
//    lateinit var button:Button
//    lateinit var textView: TextView
//    lateinit var bbutton:Button
//    var appid:String = "astergen2-ijwce"
//    val app = App(AppConfiguration.Builder(appid).build())
//    val currentUser = app.currentUser()
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login)
//        editText1 = findViewById(R.id.edittext)
//        editText2 = findViewById(R.id.edittext2)
//        editText3 = findViewById(R.id.edittext3)
//        button = findViewById(R.id.button2)
//        bbutton = findViewById(R.id.kkk)
//        textView = findViewById(R.id.readtext)
//        val mongoClient:MongoClient? = currentUser?.getMongoClient("mongodb-atlas")
//        val mongoDatabase:MongoDatabase? = mongoClient?.getDatabase("AsterData")
//        val mongoCollection:MongoCollection<Document> = mongoDatabase?.getCollection("Users") as MongoCollection<Document>
////          val nestDoc = Document("customerName", "Yeshua Galisanao").append(
////                  "customer-address", Arrays.asList(Document("primaryAddress", Arrays.asList(Document("street", "#43 Nice Street").append("city", "Niceton")
////                                  .append("state", "PH")
////                                  .append("zip", "57733"))),
////                      Document("secondaryAddress", Arrays.asList(Document("street", "#54 Easy Street").append("city", "Easyton")
////                                  .append("state", "PH")
////                                  .append("zip", "57667")))))
//        button.setOnClickListener {
//            if (editText1.text.toString().isEmpty() || editText2.text.toString().isEmpty() || editText3.text.toString().isEmpty()) {
//                Toast.makeText(this,"Fill all info",Toast.LENGTH_SHORT).show()
//            } else {
//                val doc = Document().append("name",editText1.text.toString()).append("Key",Document().append("Email",editText2.text.toString()).append("Gender",editText3.text.toString()))
//                mongoCollection.insertOne(doc).getAsync {
//                    if (it.isSuccess){
//                        Toast.makeText(this,"Success",Toast.LENGTH_SHORT).show()
//                    } else {
//                        Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//
//
//
//            //            mongoCollection.insertMany().getAsync {
////               if (it.isSuccess){
////                   Toast.makeText(this,"Success",Toast.LENGTH_SHORT).show()
////               } else{
////                   Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()
////               }
////           }
//        }
//        bbutton.setOnClickListener {
//            val query = Document()
//            query.put("name", "vishal")
//            val update = Document()
//            update.put("\$set", Document("Key.Email",editText2.text.toString()))
//            mongoCollection.updateOne(query,update).getAsync {
//                if (it.isSuccess){
//                    Toast.makeText(this,"Sucess",Toast.LENGTH_LONG).show()
//                } else {
//                    Toast.makeText(this,"Failed",Toast.LENGTH_LONG).show()
//                }
//            }
//        }
//    }
//}



//bbutton.setOnClickListener {
//    val query = Document()
//    query.put("name", "vishal")
//    val findtask: RealmResultTask<MongoCursor<Document>> = mongoCollection.find(query).iterator()
//    findtask.getAsync {
//        if (it.isSuccess){
//            val results: MongoCursor<Document> = it.get()
//            if (!results.hasNext()){
//                Toast.makeText(this,"Failed",Toast.LENGTH_LONG).show()
//            }
//            while (results.hasNext()){
//                val doc = results.next().get("Key",Document::class.java)
//                val sms = doc.get("Email")
//                textView.text = sms.toString()
//
//            }
//        }
//    }


//
//bbutton.setOnClickListener {
//    val query = Document()
//    query.put("name", "vishal")
//    mongoCollection.deleteOne(query).getAsync {
//        if (it.isSuccess){
//            Toast.makeText(this,"Sucess",Toast.LENGTH_LONG).show()
//        }
//    }















//class LoginActivity : AppCompatActivity() {
//    lateinit var editText1: EditText
//    lateinit var editText2: EditText
//    lateinit var editText3:EditText
//    lateinit var button:Button
//    lateinit var textView: TextView
//    lateinit var bbutton:Button
//    var appid:String = "astergen2-ijwce"
//    val app = App(AppConfiguration.Builder(appid).build())
//    val currentUser = app.currentUser()
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login)
//        editText1 = findViewById(R.id.edittext)
//        editText2 = findViewById(R.id.edittext2)
//        editText3 = findViewById(R.id.edittext3)
//        button = findViewById(R.id.button2)
//        bbutton = findViewById(R.id.kkk)
//        textView = findViewById(R.id.readtext)
//        val mongoClient:MongoClient? = currentUser?.getMongoClient("mongodb-atlas")
//        val mongoDatabase:MongoDatabase? = mongoClient?.getDatabase("AsterData")
//        val mongoCollection:MongoCollection<Document> = mongoDatabase?.getCollection("Users") as MongoCollection<Document>
////          val nestDoc = Document("customerName", "Yeshua Galisanao").append(
////                  "customer-address", Arrays.asList(Document("primaryAddress", Arrays.asList(Document("street", "#43 Nice Street").append("city", "Niceton")
////                                  .append("state", "PH")
////                                  .append("zip", "57733"))),
////                      Document("secondaryAddress", Arrays.asList(Document("street", "#54 Easy Street").append("city", "Easyton")
////                                  .append("state", "PH")
////                                  .append("zip", "57667")))))
//        button.setOnClickListener {
//            if (editText1.text.toString().isEmpty() || editText2.text.toString().isEmpty() || editText3.text.toString().isEmpty()) {
//                Toast.makeText(this,"Fill all info",Toast.LENGTH_SHORT).show()
//            } else {
//                val doc = Document().append("name",editText1.text.toString()).append("Key",Document().append("Email",editText2.text.toString()).append("Gender",editText3.text.toString()))
//                mongoCollection.insertOne(doc).getAsync {
//                    if (it.isSuccess){
//                        Toast.makeText(this,"Success",Toast.LENGTH_SHORT).show()
//                    } else {
//                        Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//
//
//
//            //            mongoCollection.insertMany().getAsync {
////               if (it.isSuccess){
////                   Toast.makeText(this,"Success",Toast.LENGTH_SHORT).show()
////               } else{
////                   Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()
////               }
////           }
//        }
//
//        bbutton.setOnClickListener {
//            val query = Document()
//            query.put("name", "vishal")
//            mongoCollection.deleteOne(query).getAsync {
//                if (it.isSuccess){
//                    Toast.makeText(this,"Sucess",Toast.LENGTH_LONG).show()
//                }
//            }
//
//
//
////              val query = Document()
////              query.put("name", "vishal")
////            val update = Document()
////             update.put("\$set", Document("Key.Email",editText2.text.toString()))
////            mongoCollection.updateOne(query,update).getAsync {
////                if (it.isSuccess){
////                    Toast.makeText(this,"Sucess",Toast.LENGTH_LONG).show()
////                } else {
////                    Toast.makeText(this,"Failed",Toast.LENGTH_LONG).show()
////                }
////            }
//        }
//    }
//}
//
//
 /*  class FirestorePagingSource(private val db: FirebaseFirestore, private val newText:String) : PagingSource<QuerySnapshot, fat>() {
    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, fat> {
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
}   */


/*    fun setData(perList:MutableList<MichClass>){
        this.usersList = perList.toMutableList()
    }

   fun addAll(newUsers: ArrayList<MichClass>) {
       val initsize = usersList.size
       usersList.addAll(newUsers)
       notifyItemRangeChanged(initsize, newUsers.size)

   }*/






/*
    fun setnewData(newData:ArrayList<MichClass>){
        val difClass = DifClass(usersList,newData)
        val diffresult = DiffUtil.calculateDiff(difClass)
        usersList.clear()
        usersList.addAll(newData)
        diffresult.dispatchUpdatesTo(this)
    }
*/