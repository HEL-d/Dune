package com.evw.aster

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class MichAdapternow: RecyclerView.Adapter<MichAdapternow.MichviewHolder>() {
    var usersList: MutableList<run> = arrayListOf()

     fun setData(perList:MutableList<run>){
         this.usersList = perList.toMutableList()
     }

    fun setnewData(newData:ArrayList<run>){
        val difClass = DifClass(usersList,newData)
        val diffresult = DiffUtil.calculateDiff(difClass)
        usersList.clear()
        usersList.addAll(newData)
        diffresult.dispatchUpdatesTo(this)
    }






    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MichviewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.messagelist_layout,parent,false)
        return MichviewHolder(view)
    }

    override fun getItemCount(): Int {
       return usersList.size
    }

    override fun onBindViewHolder(holder: MichviewHolder, position: Int) {
      holder.name.text = usersList[position].username
        holder.lastmessage.text = usersList[position].lastmessage
    }

    class MichviewHolder(itemView:View): RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.cc)
        var lastmessage = itemView.findViewById<TextView>(R.id.vc)

    }


}

