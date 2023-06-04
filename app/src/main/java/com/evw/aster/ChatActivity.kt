package com.evw.aster

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {


    lateinit var chatAdapter: ChatAdapter
    lateinit var recyclerView: RecyclerView
    lateinit var editText: EditText
    lateinit var imageButton: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        editText = findViewById(R.id.editText)
        imageButton = findViewById(R.id.go)
        recyclerView = findViewById(R.id.Err)
        chatAdapter = ChatAdapter()

        imageButton.setOnClickListener {
            if (editText.text.isEmpty()){
                Toast.makeText(this,"Empty",Toast.LENGTH_SHORT).show()
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    sendtoDatabase(editText.text.toString())
                }

            }

        }
    }

    private suspend fun sendtoDatabase(text: String) {







    }


}