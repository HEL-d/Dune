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


class NotificationAdapter(private val callbackInterface:CallbackInterface) : PagingDataAdapter<Notifyclass,NotificationAdapter.NotificationviewHolder>(Companion) {
    companion object : DiffUtil.ItemCallback<Notifyclass>() {
        override fun areItemsTheSame(oldItem: Notifyclass, newItem: Notifyclass): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: Notifyclass, newItem: Notifyclass): Boolean {
            return oldItem == newItem
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationviewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notification_list,parent,false)
        return NotificationviewHolder(view,callbackInterface)
    }

    override fun onBindViewHolder(holder: NotificationviewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    class NotificationviewHolder(itemview: View, private val callbackInterface: CallbackInterface): RecyclerView.ViewHolder(itemview) {
        val username = itemview.findViewById<TextView>(R.id.namecloud)
        val text = itemview.findViewById<TextView>(R.id.request)
        val maintext = itemview.findViewById<TextView>(R.id.lptext)
        val chip1 = itemview.findViewById<Chip>(R.id.accept)
        val chip2 = itemview.findViewById<Chip>(R.id.decline)
        val relativeLayout = itemview.findViewById<RelativeLayout>(R.id.rlps)
        fun bind(item: Notifyclass) {
            username.text = item.username
            maintext.text  = item.lptext
            text.text = item.requestText
            if (maintext.text.length > 0){
                chip1.visibility = View.GONE
                chip2.visibility = View.GONE
            }
            chip1.setOnClickListener {
                maintext.text = "you"
                text.text = "became friend with"
                chip1.visibility = View.GONE
                chip2.visibility = View.GONE
                callbackInterface.chip1message(item.uid.toString(),item.username.toString(),item.name.toString())
            }

            chip2.setOnClickListener {
                relativeLayout.visibility = View.GONE
              callbackInterface.chip2message(item.uid.toString())
            }






        }
    }

    interface CallbackInterface {
        fun chip1message(message: String,message2:String,message3:String)
        fun chip2message(message: String)

    }

}

