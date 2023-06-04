package com.evw.aster

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView


class MessageListAdapter:ListAdapter<run, MessageListAdapter.MessageViewHolder>(Diffutils()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.messagelist_layout,parent,false)
        return MessageViewHolder(view)

    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }


    class MessageViewHolder(itemview: View): RecyclerView.ViewHolder(itemview) {
        val name = itemview.findViewById<TextView>(R.id.cc)
        var lastmessage = itemview.findViewById<TextView>(R.id.vc)
        fun bind(item: run) {
             name.text = item.username
            lastmessage.text = item.lastmessage
        }
    }

    class Diffutils: DiffUtil.ItemCallback<run>(){
        override fun areItemsTheSame(oldItem: run, newItem: run): Boolean {
            return oldItem.roomid == newItem.roomid
        }

        override fun areContentsTheSame(oldItem: run, newItem:run): Boolean {
            return oldItem == newItem
        }

    }




}

