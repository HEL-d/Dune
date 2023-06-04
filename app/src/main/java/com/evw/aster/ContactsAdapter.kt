package com.evw.aster

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip


class ContactsAdapter(private val clickListener: (username: String) -> Unit): ListAdapter<Contact, ContactsAdapter.MyViewHolder>(Diffutils()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_contact_data,parent,false)
        return MyViewHolder(view,clickListener)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }


    class MyViewHolder(itemview: View,private val clickListener: (username:String) -> Unit): RecyclerView.ViewHolder(itemview) {

        val username = itemview.findViewById<TextView>(R.id.username)
        val name = itemview.findViewById<TextView>(R.id.name)
        val chip = itemview.findViewById<Chip>(R.id.chip_now)
        val relativeLayout = itemview.findViewById<RelativeLayout>(R.id.reply)
        fun bind(item: Contact) {
            username.text = item.username
            name.text = item.name

            chip.setOnClickListener {
                clickListener.invoke(item.username)
            }


        }
    }

    class Diffutils: DiffUtil.ItemCallback<Contact>(){
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem == newItem
        }

    }

}







