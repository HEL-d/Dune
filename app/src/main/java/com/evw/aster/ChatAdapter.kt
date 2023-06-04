package com.evw.aster

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter: PagingDataAdapter<MessageClass, ChatAdapter.ChatViewHolder>(Companion) {
    companion object : DiffUtil.ItemCallback<MessageClass>() {
        override fun areItemsTheSame(oldItem: MessageClass, newItem: MessageClass): Boolean {
            return oldItem.sendtime == newItem.sendtime
        }

        override fun areContentsTheSame(oldItem: MessageClass, newItem: MessageClass): Boolean {
            return oldItem == newItem
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chatlayout,parent,false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    class ChatViewHolder(itemview: View): RecyclerView.ViewHolder(itemview) {
        val reciver = itemview.findViewById<TextView>(R.id.receiver_message_text)
        val sender = itemview.findViewById<TextView>(R.id.sender_message_text)
        fun bind(item: MessageClass) {

          //  username.text = item.username
          //  name.text = item.name
        }
    }



}

