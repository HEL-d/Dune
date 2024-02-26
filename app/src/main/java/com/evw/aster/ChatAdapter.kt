package com.evw.aster

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChatAdapter(val context: Context): RecyclerView.Adapter<ChatAdapter.ChatviewHolder>() {
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



    fun addAll(newUsers: ArrayList<MessageClass>) {
        val initsize = messageList.size
        messageList.addAll(newUsers)
        notifyItemRangeChanged(initsize, newUsers.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatviewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.leftchat,parent,false)
        return ChatviewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatviewHolder, position: Int) {
        holder.sentMessgae.text = messageList[position].message
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


    class ChatviewHolder(itemview:View):RecyclerView.ViewHolder(itemview){
        val sentMessgae = itemView.findViewById<TextView>(R.id.sender_message_text)
    }









    /*  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
          if (holder.javaClass == SentViewHolder::class.java){
           val viewHolder = holder as SentViewHolder
            holder.sentMessgae.text = messageList[position].message

        } else {
            val viewHolder = holder as RecieverViewHolder
            holder.reciveeMessage.text = messageList[position].message
        }

      }

      override fun getItemViewType(position: Int): Int {
          val currentMeesage = messageList[position]

          if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMeesage.senderName)){
              return ITEM_SENT
          } else {
              return ITEM_RECIVE
          }


      }

      class RecieverViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
          val reciveeMessage = itemView.findViewById<TextView>(R.id.reciever_message_text)
      }


      class SentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val sentMessgae = itemView.findViewById<TextView>(R.id.sender_message_text)


    }*/






}