package com.evw.aster
import android.app.Application
import android.database.Cursor
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await


class ContactsViewModel(val mApplication: Application) : AndroidViewModel(mApplication) {
     lateinit var num:String
    private var ispassword:Boolean = false
    private val _contactsLiveData = MutableLiveData<ArrayList<Contact>>()
    val contactsLiveData: LiveData<ArrayList<Contact>> = _contactsLiveData

    fun fetchContacts() {
        viewModelScope.launch(Dispatchers.IO) {
            val contactsListAsync = async { getPhoneContacts() }
            //       val contactNumbersAsync = async { getContactNumbers() }
            //    val contactEmailAsync = async { getContactEmails() }

            val contacts = contactsListAsync.await()
            //   val contactNumbers = contactNumbersAsync.await()
            //    val contactEmails = contactEmailAsync.await()

//            contacts.forEach {
//                contactNumbers[it.id]?.let { numbers ->
//                    it.numbers = numbers
//                }
////                contactEmails[it.id]?.let { emails ->
////                    it.emails = emails
////                }
//            }
            _contactsLiveData.postValue(contacts)
        }
    }


    private suspend fun getPhoneContacts(): ArrayList<Contact> {
        val contactsList = ArrayList<Contact>()
        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.PHOTO_ID
        )
        val contactsCursor = mApplication.contentResolver?.query(
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
                    CommonDataKinds.Phone.CONTACT_ID,
                    CommonDataKinds.Phone.DISPLAY_NAME,
                    CommonDataKinds.Phone.NUMBER
                )
                val phoneCursor: Cursor? = mApplication.contentResolver.query(
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
                        if (number.length > 10){
                           num = number.substring(3)
                        } else {
                            num = number
                        }
                        numbers.add(num)
                        CoroutineScope(Dispatchers.IO).launch {
                         //   getUser(num,id,name,numbers,contactsList)

                         /**   getUser(num).collect{
                                val list: ArrayList<String> = ArrayList()
                                 val phone = it.id
                                 val username = it.get("username")
                                list.add(phone)
                                 if (numbers.contains(phone) && list.contains(phone)){
                                     contactsList.add(Contact(id,name,phone.toString(),username.toString()))
                                 }
                            }   **/


                        }




//                        val registration = FirebaseFirestore.getInstance().collection("Contacts").whereEqualTo("phoneNumber",num)
//                        registration.addSnapshotListener { value, error ->
//                          if (value != null){
//                              val list: ArrayList<String> = ArrayList()
//                              for (document in value){
//                                  val phone = document.id
//                                  val pass = document.get("password")
//                                  list.add(phone.toString())
//                                  if (numbers.contains(phone) && list.contains(phone)){
//                                      contactsList.add(Contact(id,name,phone.toString(),pass.toString()))
//                                  }
//
//
//
//
//                              }
//
//                          }
//
//
//                        }

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
    ):QuerySnapshot? {
        return try {
            val doc = FirebaseFirestore.getInstance().collection("Contacts").whereIn("phoneNumber",numbers).get().await()
            if(doc != null){
              for (data in doc){
                  send(data,id,name,numbers,contactsList)
              }
            }
            doc
        } catch (e:Exception){
            null
        }


    }

    private fun send(
        data: QueryDocumentSnapshot?,
        id: String,
        name: String,
        numbers: ArrayList<String>,
        contactsList: ArrayList<Contact>
    ) {
        val list: ArrayList<String> = ArrayList()
        val phone = data?.id
        val username = data?.get("username")
     //   contactsList.add(Contact(id,name,phone.toString(),username.toString()))
//        list.add(phone!!)
//        if (numbers.contains(phone) && list.contains(phone)){
//            contactsList.add(Contact(id,name,phone.toString(),username.toString()))
//        }


    }


    /**   fun getUser(num: String): Flow<DocumentSnapshot> = callbackFlow {
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

                } else {

                }
            }
        }
        val registration = FirebaseFirestore.getInstance().collection("Contacts").whereEqualTo("phoneNumber",num).addSnapshotListener(listener)
        awaitClose { registration.remove() }
    }    **/








    }
















//    private suspend fun getContactNumbers() : HashMap<String, ArrayList<String>>{
//        val projection = arrayOf(CommonDataKinds.Phone.CONTACT_ID, CommonDataKinds.Phone.DISPLAY_NAME, CommonDataKinds.Phone.NUMBER)
//        val selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + " = '91'"
//        val contactsNumberMap = HashMap<String, ArrayList<String>>()
//        val phoneCursor: Cursor? = mApplication.contentResolver.query(
//            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//            projection,
//            null,
//            null,
//            null)
//        if (phoneCursor != null && phoneCursor.count > 0) {
//            val contactIdIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
//            val numberIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
//
//            while (phoneCursor.moveToNext()) {
//                val contactId = phoneCursor.getString(contactIdIndex)
//                    val number: String = phoneCursor.getString(numberIndex)
//
//
//                //check if the map contains key or not, if not then create a new array list with number
//                if (contactsNumberMap.containsKey(contactId)) {
//                    contactsNumberMap[contactId]?.add(number)
//
//               } else {
//                   contactsNumberMap[contactId] = arrayListOf(number)
//                }
//            }
//            //contact contains all the number of a particular contact
//            phoneCursor.close()
//        }
//        return contactsNumberMap
//    }

//    private suspend fun getContactEmails(): HashMap<String, ArrayList<String>> {
//        val contactsEmailMap = HashMap<String, ArrayList<String>>()
//        val emailCursor = mApplication.contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
//            null,
//            null,
//            null,
//            null)
//        if (emailCursor != null && emailCursor.count > 0) {
//            val contactIdIndex = emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID)
//            val emailIndex = emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)
//            while (emailCursor.moveToNext()) {
//                val contactId = emailCursor.getString(contactIdIndex)
//                val email = emailCursor.getString(emailIndex)
//                //check if the map contains key or not, if not then create a new array list with email
//                if (contactsEmailMap.containsKey(contactId)) {
//                    contactsEmailMap[contactId]?.add(email)
//                } else {
//                    contactsEmailMap[contactId] = arrayListOf(email)
//                }
//            }
//            //contact contains all the emails of a particular contact
//            emailCursor.close()
//        }
//        return contactsEmailMap
  //  : HashMap<String, ArrayList<String>>
//    }



