package com.evw.aster

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BAdapter (val context: Context): RecyclerView.Adapter<BAdapter.BViewHolder>() {
    var messageList: MutableList<MessageClass> = arrayListOf()


    fun addlisttop(list: ArrayList<MessageClass>) {
        messageList.clear()
        messageList.addAll(0,list)
        notifyItemRangeInserted(0,list.size)
        notifyItemChanged(list.size)


        /* for (i in list.indices) {
             messageList.add(i, list[i])
             notifyItemInserted(i)
         }*/
    }



    /*val ITEM_RECIVE = 1
      val ITEM_SENT = 2*/

    fun addAll(newUsers: ArrayList<MessageClass>) {
        val initsize = messageList.size
        messageList.clear()
        messageList.addAll(newUsers)
        notifyItemRangeChanged(initsize, newUsers.size)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.chatlayout,parent,false)
        return BViewHolder(view)
    }

    override fun onBindViewHolder(holder: BViewHolder, position: Int) {
        holder.reciveeMessage.text = messageList[position].message
    }


    /*  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
          if (viewType == 1){
              val view: View = LayoutInflater.from(context).inflate(R.layout.chatlayout,parent,false)
              return RecieverViewHolder(view)
          } else {
              val view: View = LayoutInflater.from(context).inflate(R.layout.leftchat,parent,false)
              return SentViewHolder(view)
          }

      }*/



    override fun getItemCount(): Int {
        return messageList.size
    }


    class BViewHolder(itemview: View): RecyclerView.ViewHolder(itemview){
        val reciveeMessage = itemView.findViewById<TextView>(R.id.reciever_message_text)
    }

}