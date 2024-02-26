package com.evw.aster

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener

class ChatActivity : AppCompatActivity() {

    var uid:String? = null
    lateinit var chatAdapter: ChatAdapter
    lateinit var recyclerView: RecyclerView
    lateinit var recylerview2:RecyclerView
    lateinit var editText: EditText
    lateinit var imageButton: ImageButton
    lateinit var BAdapter:BAdapter
    var reciverRoom:String? = null
    var senderRoom:String? = null
    lateinit var mlist:ArrayList<MessageClass>
    lateinit var ulist:ArrayList<MessageClass>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        editText = findViewById(R.id.editText)
        imageButton = findViewById(R.id.go)
        recyclerView = findViewById(R.id.Err)
        recylerview2 = findViewById(R.id.prr)
        recyclerView.layoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL,false)
        ulist = arrayListOf()
        mlist = arrayListOf()

        uid = FirebaseAuth.getInstance().currentUser?.uid
        val reciveruid = intent.getStringExtra("vid")
        senderRoom = reciveruid + uid
        reciverRoom = uid + reciveruid
       chatAdapter = ChatAdapter(this)
        BAdapter = BAdapter(this)
        recyclerView.adapter = chatAdapter
        recylerview2.adapter = BAdapter
        imageButton.setOnClickListener {
            if (editText.text.isEmpty()){
                Toast.makeText(this,"Empty",Toast.LENGTH_SHORT).show()
            } else {
                sendData(editText.text.toString())
            }
        }
        getDatafromDatabase()
        getDatareciverroom()


    }

    private fun getDatareciverroom() {
        FirebaseDatabase.getInstance().getReference("Message2").child(reciverRoom.toString()).addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                mlist.clear()
                for (datasnap in snapshot.children){
                    val bc = datasnap.getValue(MessageClass::class.java)
                    mlist.add(bc!!)
                }
                BAdapter.addAll(mlist)
                BAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun getDatafromDatabase() {
        FirebaseDatabase.getInstance().getReference("Message").child(senderRoom.toString()).addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                ulist.clear()
              for (datasnap in snapshot.children){
                  val bc = datasnap.getValue(MessageClass::class.java)
                  ulist.add(bc!!)
              }

                chatAdapter.addAll(ulist)
                chatAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })



    }

    private fun sendData(p: String) {

         val st =  -1 * (ServerValue.TIMESTAMP).toString().toLong()
        val vp = MessageClass(uid,p,st)
        FirebaseDatabase.getInstance().getReference("Message").child(senderRoom.toString()).push().setValue(vp).addOnSuccessListener {
            FirebaseDatabase.getInstance().getReference("Message2").child(senderRoom.toString()).push().setValue(vp)
        }
        editText.setText("")



    }







}