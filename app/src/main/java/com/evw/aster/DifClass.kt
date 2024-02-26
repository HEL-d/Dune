package com.evw.aster

import androidx.recyclerview.widget.DiffUtil

class DifClass(private val oldList: List<run>, private val newList: List<run>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].roomid === newList[newItemPosition].roomid
    }
    override fun areContentsTheSame(oldCourse: Int, newPosition: Int): Boolean {
        val (_, value, name) = oldList[oldCourse]
        val (_, value1, name1) = newList[newPosition]
        return name == name1 && value == value1
    }



}