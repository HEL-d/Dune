package com.evw.aster

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip

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
        return FriendListViewHolder(view,friendInterface)
    }

    override fun onBindViewHolder(holder: FriendListViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    class FriendListViewHolder(itemview: View, private val friendInterface:MInterface): RecyclerView.ViewHolder(itemview) {
        val username = itemview.findViewById<TextView>(R.id.namecloud)
        val text = itemview.findViewById<TextView>(R.id.name_v)
        val chip1 = itemview.findViewById<Chip>(R.id.remove)
        val relativeLayout = itemview.findViewById<RelativeLayout>(R.id.frrel)
        fun bind(item: FriendListClass) {
            username.text = item.username
            text.text = item.name
            chip1.setOnClickListener {
                friendInterface.chip1message(item.uid.toString(),item.roomid.toString())
            }


        }
    }

    interface MInterface {
        fun chip1message(uid: String, roomid: String)

    }

}


