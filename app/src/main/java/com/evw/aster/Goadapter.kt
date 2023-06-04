package com.evw.aster

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class Goadapter: PagingDataAdapter<fat, Goadapter.GoviewHolder>(Companion) {
    companion object : DiffUtil.ItemCallback<fat>() {
        override fun areItemsTheSame(oldItem: fat, newItem: fat): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: fat, newItem: fat): Boolean {
            return oldItem == newItem
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoviewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewsearch,parent,false)
        return GoviewHolder(view)
    }

    override fun onBindViewHolder(holder: GoviewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    class GoviewHolder(itemview: View): RecyclerView.ViewHolder(itemview) {
        val username = itemview.findViewById<TextView>(R.id.bias)
        val name = itemview.findViewById<TextView>(R.id.hasd)
        fun bind(item: fat) {
            username.text = item.username
            name.text = item.name
        }
    }



    }