package com.evw.aster

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView


class CoAdapter : PagingDataAdapter<ava, CoAdapter.CoviewHolder>(Companion) {
    companion object : DiffUtil.ItemCallback<ava>() {
        override fun areItemsTheSame(oldItem: ava, newItem: ava): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: ava, newItem: ava): Boolean {
            return oldItem == newItem
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoviewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_contact,parent,false)
        return CoviewHolder(view)
    }

    override fun onBindViewHolder(holder: CoviewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    class CoviewHolder(itemview: View): RecyclerView.ViewHolder(itemview) {
        val username = itemview.findViewById<TextView>(R.id.username)
        val name = itemview.findViewById<TextView>(R.id.name)
        fun bind(item: ava) {
            username.text = item.username
            name.text = item.name
        }
    }



}
