package com.evw.aster

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView


class MarkusAdapter:RecyclerView.Adapter<MarkusAdapter.MarkusviewHolder>() {
    var userList = ArrayList<run>()

    fun setData(newdata: ArrayList<run>) {
        val difClass = DifClass(userList, newdata)
        val diffresult = DiffUtil.calculateDiff(difClass)
        userList.clear()
        userList.addAll(newdata)
        diffresult.dispatchUpdatesTo(this)

    }

    fun getLastItemDate():String? {
        return userList[userList.size - 1].roomid
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):MarkusviewHolder  {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.messagelist_layout,parent,false)
        return MarkusviewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: MarkusviewHolder, position: Int) {
        holder.name.text = userList[position].username
        holder.lastmessage.text = userList[position].lastmessage
    }

    class MarkusviewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.cc)
        var lastmessage = itemView.findViewById<TextView>(R.id.vc)

    }



}