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

class ViasAdapter : PagingDataAdapter<run, ViasAdapter.ViasViewHolder>(Companion) {
    companion object : DiffUtil.ItemCallback<run>() {
        override fun areItemsTheSame(oldItem: run, newItem: run): Boolean {
            return oldItem.roomid == newItem.roomid
        }

        override fun areContentsTheSame(oldItem: run, newItem: run): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViasViewHolder{
        val view = LayoutInflater.from(parent.context).inflate(R.layout.messagelist_layout, parent, false)
        return ViasViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViasViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    class ViasViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val name = itemView.findViewById<TextView>(R.id.cc)
        var lastmessage = itemView.findViewById<TextView>(R.id.vc)
        fun bind(item: run) {
           name.text = item.username
            lastmessage.text = item.lastmessage


        }
    }



}