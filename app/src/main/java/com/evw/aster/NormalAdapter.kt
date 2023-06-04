package com.evw.aster

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.evw.aster.NormalAdapter.NormalviewHolder
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class NormalAdapter(options: FirebaseRecyclerOptions<NormalClass>) :
    FirebaseRecyclerAdapter<NormalClass, NormalviewHolder>(options) {
    override fun onBindViewHolder(holder: NormalviewHolder, position: Int, model: NormalClass) {
        holder.username.text = model.username
        holder.lastmessage.text = model.lastmessage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NormalviewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.messagelist_layout, parent, false)
        return NormalviewHolder(view)
    }

    class NormalviewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var username: TextView
        var lastmessage: TextView

        init {
            username = itemView.findViewById(R.id.cc)
            lastmessage = itemView.findViewById(R.id.vc)
        }
    }
}