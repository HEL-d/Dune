package com.evw.aster

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso


class MichAdapternow(private val michInterface:MichInterface):
    ListAdapter<MichClass, MichAdapternow.MichviewHolder>(Diffutils()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MichviewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.messagelist_layout,parent,false)
        return MichviewHolder(view,michInterface,parent.context)
    }



    override fun onBindViewHolder(holder: MichviewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }






    }

    class MichviewHolder(itemView:View,private val michInterface:MichInterface,private val context: Context): RecyclerView.ViewHolder(itemView) {
        val name = itemView.findViewById<TextView>(R.id.cc)
        var lastmessage = itemView.findViewById<TextView>(R.id.vc)
        val relativeLayout = itemView.findViewById<RelativeLayout>(R.id.mich_rel)
        val imageview = itemView.findViewById<ImageView>(R.id.contactdata_view)
        var textview = itemView.findViewById<TextView>(R.id.no_count)
        var getsnap:String? = null
        var uid = FirebaseAuth.getInstance().currentUser?.uid
        var username:String? = null
        fun bind(item:MichClass){
            name.text = item.name
            FirebaseFirestore.getInstance().collection("Users").document(item.vid.toString()).get().addOnCompleteListener {
                val imaage = it.result.get("profilepic")
                val usernames = it.result.get("username")
                username = usernames.toString()
                getsnap = imaage.toString()

                if (imaage == null){
                    imageview.setImageResource(R.drawable.blankprofile)
                } else {
                    Picasso.get().load(imaage.toString()).placeholder(R.drawable.blankprofile).into(imageview)
                }
            }

            lastmessage.text = item.lastmessage
            val isread = item.isread
            if (!isread){
                lastmessage.setTextColor(ContextCompat.getColor(context,R.color.Aster_neo))
                textview.visibility = View.VISIBLE
            } else {
                lastmessage.setTextColor(ContextCompat.getColor(context,R.color.palewhite))
                textview.visibility = View.GONE
            }

            relativeLayout.setOnClickListener {
              michInterface.nextActivity(item.vid.toString(),item.roomname.toString())
            }


            relativeLayout.setOnLongClickListener {
                michInterface.getrac(item.vid.toString(),username.toString(),getsnap.toString())
                false
            }






        }





    }



    class Diffutils: DiffUtil.ItemCallback<MichClass>(){
        override fun areItemsTheSame(oldItem: MichClass, newItem: MichClass): Boolean {
            return oldItem.timestamp == newItem.timestamp
        }

        override fun areContentsTheSame(oldItem: MichClass, newItem: MichClass): Boolean {
            return oldItem == newItem
        }

    }






  interface MichInterface {
      fun nextActivity(roomid:String,roomname:String)
      fun getrac(roomid: String,username:String,profilepic:String)
  }
}

