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


class CoAdapter(private val clickListener: (username: String,profilepic:String,uid:String,avatarurl:String) -> Unit,private val actionblock: Actionblock) : PagingDataAdapter<KurtClass, CoAdapter.CoviewHolder>(Companion) {
    companion object : DiffUtil.ItemCallback<KurtClass>() {
        override fun areItemsTheSame(oldItem: KurtClass, newItem: KurtClass): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: KurtClass, newItem: KurtClass): Boolean {
            return oldItem == newItem
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoviewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_contact,parent,false)
        return CoviewHolder(view,clickListener,parent.context,actionblock)
    }

    override fun onBindViewHolder(holder: CoviewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    class CoviewHolder(
        itemview: View,
        private val clickListener: (username: String, profilepic: String, uid: String, avatarurl: String) -> Unit,
        private val context: Context,
       private val actionblock: Actionblock
    ): RecyclerView.ViewHolder(itemview) {
        val username = itemview.findViewById<TextView>(R.id.username)
        val name = itemview.findViewById<TextView>(R.id.name)
        val imageview = itemview.findViewById<ImageView>(R.id.rowcontact_view)
        val chip = itemview.findViewById<Chip>(R.id.chip_now)
        val relativeLayout = itemview.findViewById<RelativeLayout>(R.id.qaz)
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        fun bind(item: KurtClass) {
            username.text = item.username
            name.text = item.name
            if (item.profilepic == null){
                imageview.setImageResource(R.drawable.blankprofile)
                item.avatarurl = "null"
            } else {
                Picasso.get().load(item.profilepic).into(imageview)
            }

            chip.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val blocklocation = FirebaseFirestore.getInstance().collection("Blockaccounts").document(item.uid.toString()).collection("accounts").document(uid.toString()).get().await()
                    withContext(Dispatchers.Main){
                        if (blocklocation.exists()){
                            Toast.makeText(context,"Could not perform this action", Toast.LENGTH_LONG).show()
                        }else {
                            clickListener.invoke(item.username.toString(),item.profilepic.toString(),item.uid.toString(),item.avatarurl.toString())
                        }
                    }
                }
            }

            relativeLayout.setOnLongClickListener {
                actionblock.blockaccountnow(item.uid.toString(),item.username.toString(),item.profilepic.toString())
                return@setOnLongClickListener false
            }



        }
    }

  interface Actionblock{
      fun blockaccountnow(cid:String,username: String,profilepic: String)
  }

}
