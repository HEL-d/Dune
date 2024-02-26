package com.evw.aster

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class Goadapter(private val searchInerface:SearchInerface): PagingDataAdapter<Searchavatarclass, Goadapter.GoviewHolder>(Companion) {
    companion object : DiffUtil.ItemCallback<Searchavatarclass>() {
        override fun areItemsTheSame(oldItem: Searchavatarclass, newItem: Searchavatarclass): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: Searchavatarclass, newItem: Searchavatarclass): Boolean {
            return oldItem == newItem
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoviewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewsearch,parent,false)
        return GoviewHolder(view,searchInerface,parent.context)
    }

    override fun onBindViewHolder(holder: GoviewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    class GoviewHolder(itemview: View, private val searchInerface: SearchInerface, private val context: Context): RecyclerView.ViewHolder(itemview) {
        val username = itemview.findViewById<TextView>(R.id.bias)
        val name = itemview.findViewById<TextView>(R.id.hasd)
        val imageview = itemview.findViewById<ImageView>(R.id.serachprofile_view)
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        fun bind(item: Searchavatarclass) {
            username.text = item.username
            name.text = item.name
             if (item.profilepic == null){
                 imageview.setImageResource(R.drawable.blankprofile)
                 item.avatarurl = "null"
             } else {
                 Picasso.get().load(item.profilepic).into(imageview)
             }
            imageview.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val blocklocation = FirebaseFirestore.getInstance().collection("Blockaccounts").document(item.uid.toString()).collection("accounts").document(uid.toString()).get().await()
                    withContext(Dispatchers.Main){
                        if (blocklocation.exists()){
                            Toast.makeText(context,"Could not perform this action", Toast.LENGTH_LONG).show()
                        }else {
                            searchInerface.sendtonew(item.username.toString(),item.uid.toString(),item.profilepic.toString(),item.avatarurl.toString())
                        }
                    }
                }

            }

        }
    }

    interface SearchInerface {
        fun sendtonew(username:String,uid:String,profilepic:String,avatarurl:String)
    }



}



