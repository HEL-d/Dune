package com.evw.aster
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class ContactsyncActivity : AppCompatActivity() {
    lateinit var viewDialog: ViewDialog
     lateinit var textView: TextView
    var num:String? = null
    lateinit var recyclerView: RecyclerView
    private val CONTACTS_READ_REQ_CODE = 100
    lateinit var adapter: ContactsAdapter
    lateinit var recyclerView2: RecyclerView
    lateinit var adapter2: CoAdapter
      lateinit var progressBar1: MaterialNeonProgressBar
    lateinit var progressBar2: MaterialNeonProgressBar
    var username:String? = null
    var name:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contactsync)
        recyclerView = findViewById(R.id.rvContacts)
        recyclerView2 = findViewById(R.id.recylerview_kl)
        viewDialog = ViewDialog()
        recyclerView2.setHasFixedSize(false)
        progressBar1 = findViewById(R.id.progressBar_z)
        progressBar2 = findViewById(R.id.progressBarLoadMore)
        adapter2 = CoAdapter()
        CoroutineScope(Dispatchers.IO).launch {
            getUsername()
        }
        CoroutineScope(Dispatchers.IO).launch {
            getNamenow()
        }

        recyclerView2.adapter = adapter2
        textView = findViewById(R.id.quick_add)
        init()
        lifecycleScope.launch {
            adapter2.loadStateFlow.collectLatest {
           //     progressBar1.isVisible = it.refresh is LoadState.Loading

                progressBar2.isVisible = it.append is LoadState.Loading
            }
       }





    }

    private suspend fun getNamenow():DocumentSnapshot? {
        return try {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val doc = FirebaseFirestore.getInstance().collection("Users").document(uid.toString()).get().await()
            val dbusername = doc.get("name")
            name = dbusername.toString()
            doc
        } catch (e:Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@ContactsyncActivity, "Nothing found", Toast.LENGTH_SHORT).show()
            }
            null
        }
    }

    private suspend fun getUsername() :DocumentSnapshot? {
        return try {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val doc = FirebaseFirestore.getInstance().collection("Users").document(uid.toString()).get().await()
            val dbusername = doc.get("username")
            username = dbusername.toString()
            doc
        } catch (e:Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@ContactsyncActivity, "Nothing found", Toast.LENGTH_SHORT).show()
            }
            null
        }

    }


    private fun init() {
        adapter = ContactsAdapter{
            CoroutineScope(Dispatchers.IO).launch {
                sendfriendrequest(it)
            }
        }
        if (hasPermission(android.Manifest.permission.READ_CONTACTS)) {
            fetchContacts()
             fetchusers()

       } else {
            requestPermissionWithRationale(android.Manifest.permission.READ_CONTACTS, CONTACTS_READ_REQ_CODE, getString(R.string.contact_permission_rationale))
        }
    }

   private suspend fun sendfriendrequest(username: String):DocumentSnapshot? {
        return try {
            val doc = FirebaseFirestore.getInstance().collection("Usersname").document(username).get().await()
            val vid = doc.get("uid")
            startsendingfr(vid.toString())
            doc
        } catch (e:Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@ContactsyncActivity, "Nothinf found", Toast.LENGTH_SHORT).show()
            }
            null
        }


    }

    private suspend fun startsendingfr(vid: String): Boolean {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        return try {
            if (uid == vid){
                withContext(Dispatchers.Main){
                    Toast.makeText(this@ContactsyncActivity,"same uid",Toast.LENGTH_SHORT).show()
                }
            } else {
                val data = hashMapOf("username" to username, "uid" to uid, "lptext" to "","name" to name,"requestText"
                to "sent you request","timestamp" to FieldValue.serverTimestamp(),"isread" to "false")
                val db = FirebaseFirestore.getInstance().collection("Notifications").document(vid).collection("notify")
                    .document(uid.toString()).set(data).await()
            }
            true

        } catch (e:Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@ContactsyncActivity,"Failed to send",Toast.LENGTH_SHORT).show()
            }
            false
        }

    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CONTACTS_READ_REQ_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchContacts()
           fetchusers()

        }
    }


    private fun fetchContacts() {
        lifecycleScope.launch(Dispatchers.IO) {
            getPhoneContacts()
       }

    }

    private fun fetchusers() {
       lifecycleScope.launch(Dispatchers.IO){
            val flow = Pager(PagingConfig(1)) {
                NewPagingSource(FirebaseFirestore.getInstance())
            }.flow.collect{
                withContext(Dispatchers.Main){
                    adapter2.submitData(it)


                }
            }
        }
    }
    private suspend  fun getPhoneContacts(): ArrayList<Contact> {
        val contactsList = ArrayList<Contact>()
        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.PHOTO_ID)
        val contactsCursor = contentResolver?.query(
            ContactsContract.Contacts.CONTENT_URI,
            projection,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC")
        if (contactsCursor != null && contactsCursor.count > 0) {
            val idIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts._ID)
            val nameIndex = contactsCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            while (contactsCursor.moveToNext()) {
                val id = contactsCursor.getString(idIndex)
                val name = contactsCursor.getString(nameIndex)
                val projection1 = arrayOf(
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                )
                val phoneCursor: Cursor? = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    projection1,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    arrayOf(id),
                    null
                )
                if (phoneCursor != null && phoneCursor.count > 0) {
                    val numbers: ArrayList<String> = ArrayList()
                    val numberIndex =
                        phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    while (phoneCursor.moveToNext()) {
                        val number: String = phoneCursor.getString(numberIndex)
                     //   contactsList.add(Contact(id,name,number))
                        if (number.length > 10){
                            num = number.substring(3)
                        } else {
                            num = number
                        }
                        numbers.add(num!!)
                        CoroutineScope(Dispatchers.IO).launch {
                            getUser(num!!,id,name,numbers,contactsList)
                        }

                    }
                    phoneCursor.close()
                }

            }
            contactsCursor.close()

        }

        return contactsList
    }


    private suspend fun getUser(
        num: String,
        id: String,
        name: String,
        numbers: ArrayList<String>,
        contactsList: ArrayList<Contact>
    ): QuerySnapshot? {
        return try {
            val doc = FirebaseFirestore.getInstance().collection("Contacts").whereIn("phoneNumber",numbers).get().await()
            if(!doc.isEmpty){
                for (data in doc){
                    send(data,id,name,numbers,contactsList)
                }
            } else {
                withContext(Dispatchers.Main){
                    progressBar1.visibility = View.GONE
                }
            }
            doc
        } catch (e:Exception){
            withContext(Dispatchers.Main){
                progressBar1.visibility = View.GONE
            }
            null
        }



    }

    private suspend fun send(
        data: QueryDocumentSnapshot?,
        id: String,
        name: String,
        numbers: ArrayList<String>,
        contactsList: ArrayList<Contact>
    ) {
        val list: ArrayList<String> = ArrayList()
        val phone = data?.id
        val username = data?.get("username")
        contactsList.add(Contact(id,name,phone.toString(),username.toString()))

        withContext(Dispatchers.Main){
            progressBar1.visibility = View.GONE
            adapter.submitList(contactsList)
            recyclerView.adapter = adapter
        }


    }

    class NewPagingSource(private val db: FirebaseFirestore) : PagingSource<QuerySnapshot, ava>() {
        override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, ava> {
            return try {
                val currentPage = params.key ?: db.collection("Usersname").limit(8).get().await()
                val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]
                val nextPage = db.collection("Usersname")
                    .limit(8).startAfter(lastDocumentSnapshot).get()
                    .await()

                LoadResult.Page(
                    data = currentPage.toObjects(ava::class.java),
                    prevKey = null,
                    nextKey = nextPage
                )
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }

        override fun getRefreshKey(state: PagingState<QuerySnapshot, ava>): QuerySnapshot? {
            return null
        }
    }



}