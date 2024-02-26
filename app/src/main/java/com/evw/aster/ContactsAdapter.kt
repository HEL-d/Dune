package com.evw.aster
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class ContactsAdapter(private val clickListener: (username: String,profilepic:String,uid:String,avatarurl:String) -> Unit,private val actionhere: Actionhere): ListAdapter<Contact, ContactsAdapter.MyViewHolder>(Diffutils()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_contact_data,parent,false)
        return MyViewHolder(view,clickListener,parent.context,actionhere)

    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }


    class MyViewHolder(
        itemview: View,
        private val clickListener: (username: String, profilepic: String, uid: String, avatarurl: String) -> Unit,
        private val context: Context,
     private val  actionhere: Actionhere): RecyclerView.ViewHolder(itemview) {
        val username = itemview.findViewById<TextView>(R.id.username)
        val name = itemview.findViewById<TextView>(R.id.name)
        val chip = itemview.findViewById<Chip>(R.id.chip_now)
        val relativeLayout = itemview.findViewById<RelativeLayout>(R.id.reply)
        val imageview = itemview.findViewById<ImageView>(R.id.contactdata_view)
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        fun bind(item: Contact) {
            username.text = item.username
            name.text = item.name
            chip.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val blocklocation = FirebaseFirestore.getInstance().collection("Blockaccounts").document(item.uid.toString()).collection("accounts").document(uid.toString()).get().await()
                    withContext(Dispatchers.Main){
                        if (blocklocation.exists()){
                            Toast.makeText(context,"Could not perform this action",Toast.LENGTH_LONG).show()
                        }else {
                            clickListener.invoke(item.username,item.profilepic.toString(),item.uid.toString(),item.avatarurl.toString())
                        }
                    }
                }
            }
            if (item.profilepic == null){
                imageview.setImageResource(R.drawable.blankprofile)
                item.avatarurl = "null"
            } else {
                Picasso.get().load(item.profilepic).placeholder(R.drawable.blankprofile).into(imageview)
            }

             relativeLayout.setOnLongClickListener {
                actionhere.blockcontact(item.uid.toString(),item.username.toString(),item.profilepic.toString())
                 return@setOnLongClickListener false
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



    interface Actionhere{
        fun blockcontact(cid:String,username: String,profilepic: String)
    }










}







