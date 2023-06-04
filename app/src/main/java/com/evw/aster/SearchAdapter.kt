package com.evw.aster

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class SearchAdapter:ListAdapter <fat,SearchAdapter.SearchviewHolder>(Diffutils()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchviewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewsearch,parent,false)
        return SearchviewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchviewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class SearchviewHolder(itemview:View):RecyclerView.ViewHolder(itemview) {
        val username = itemview.findViewById<TextView>(R.id.bias)
        val name = itemview.findViewById<TextView>(R.id.hasd)
        fun bind(item: fat) {
            username.text = item.username
            name.text = item.name
        }
    }

    class Diffutils: DiffUtil.ItemCallback<fat>(){
        override fun areItemsTheSame(oldItem: fat, newItem: fat): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: fat, newItem: fat): Boolean {
            return oldItem == newItem
        }

    }


}