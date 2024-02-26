package com.evw.aster


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FriendListAdapter (private val friendInterface:MInterface) : PagingDataAdapter<FriendListClass, FriendListAdapter.FriendListViewHolder>(Companion) {
    companion object : DiffUtil.ItemCallback<FriendListClass>() {
        override fun areItemsTheSame(oldItem: FriendListClass, newItem: FriendListClass): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: FriendListClass, newItem: FriendListClass): Boolean {
            return oldItem == newItem
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.friendslist,parent,false)
        return FriendListViewHolder(view,friendInterface,parent.context)
    }

    override fun onBindViewHolder(holder: FriendListViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    class FriendListViewHolder(
        itemview: View,
        private val friendInterface: MInterface,
       private val context: Context
    ): RecyclerView.ViewHolder(itemview) {
        val username = itemview.findViewById<TextView>(R.id.namecloud)
        val text = itemview.findViewById<TextView>(R.id.name_v)
        val chip1 = itemview.findViewById<Chip>(R.id.remove)
        val relativeLayout = itemview.findViewById<RelativeLayout>(R.id.frrel)
        val imageview = itemview.findViewById<ImageView>(R.id.friendsprofile_view)
        var getsnap:String? = null
        var uid = FirebaseAuth.getInstance().currentUser?.uid
        fun bind(item: FriendListClass) {
            username.text = item.username
            text.text = item.name
            CoroutineScope(Dispatchers.IO).launch {
                val imagelocation = FirebaseFirestore.getInstance().collection("Users").document(item.uid.toString()).get().await()
                val imaage = imagelocation.get("profilepic")
                getsnap = imaage.toString()
                withContext(Dispatchers.Main){
                    if (imaage == null){
                        imageview.setImageResource(R.drawable.blankprofile)
                    } else {
                        Picasso.get().load(imaage.toString()).placeholder(R.drawable.blankprofile).into(imageview)
                    }
                }

            }
            chip1.setOnClickListener {
                friendInterface.chip1message(item.uid.toString())
            }

            relativeLayout.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val blocklocation = FirebaseFirestore.getInstance().collection("Blockaccounts").document(item.uid.toString()).collection("accounts").document(uid.toString()).get().await()
                    withContext(Dispatchers.Main){
                        if (blocklocation.exists()){
                            Toast.makeText(context,"Could not perform this action", Toast.LENGTH_LONG).show()
                        }else {
                            friendInterface.gotosecondprofile(item.name.toString(),item.uid.toString(),item.username.toString())
                        }
                    }
                }




            }

            relativeLayout.setOnLongClickListener {
             friendInterface.blockhere(item.uid.toString(),item.username.toString(),getsnap.toString())
                return@setOnLongClickListener false

            }



        }
    }

    interface MInterface {
        fun chip1message(roomid: String)
        fun gotosecondprofile(name:String,uid:String,username:String)
        fun blockhere(id: String,username: String,profilepic:String)


    }

}


