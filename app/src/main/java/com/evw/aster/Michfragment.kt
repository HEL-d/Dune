package com.evw.aster


import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AbsListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caverock.androidsvg.SVGImageView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class Michfragment : Fragment(),MichAdapternow.MichInterface {

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: MichAdapternow
    lateinit var textView: TextView
    lateinit var lm: LinearLayoutManager
    lateinit var list:ArrayList<MichClass>
    lateinit var materialNeonProgressBar: MaterialNeonProgressBar
    lateinit var svgImageView: SVGImageView
    lateinit var svgImageView2: SVGImageView
     var myurl:String? = null
    var michvalue:String? = null
    var urlpart:String? = null
    //   lateinit var list:ArrayList<run>
    var uid = FirebaseAuth.getInstance().currentUser?.uid
    var last_key = ""
    var last_node: String? = ""
    var isMaxData = false
    var isScrolling: Boolean = false
    var ITEM_LOAD_COUNT = 8
    var currentitems = 0
    var tottalitems: kotlin.Int = 0
    var scrolledoutitems: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_michfragment, container, false)

        list = arrayListOf()
        recyclerView = view.findViewById(R.id.recyclerView_mich)
        svgImageView = view.findViewById(R.id.SVGImageView6)
        svgImageView2 = view.findViewById(R.id.search_po)
      //  chip2 = view.findViewById(R.id.nmo2)
        lm = LinearLayoutManager(context, RecyclerView.VERTICAL,false)
        materialNeonProgressBar = view.findViewById(R.id.progressBarmich)
        recyclerView.layoutManager = lm
        adapter = MichAdapternow(this)
        recyclerView.adapter = adapter
         getdata()
           FirebaseFirestore.getInstance().collection("Users").document(uid.toString()).get().addOnCompleteListener {
               val url = it.result.get("avatarurl")
               val mich = it.result.get("michHeight")
               if (url != null){
                   myurl = url.toString()
                   val tpp = myurl!!.substringAfter("https://models.readyplayer.me/")
                   urlpart = tpp.replace(".glb","")
               } else {
                   myurl = "nul"
               }

               if (mich != null){
                   michvalue = mich.toString()
               }else {
                   michvalue = "null"
               }

           }

        svgImageView.setOnClickListener {
           startActivity(Intent(context,ProfileActivity::class.java))
        }

        svgImageView2.setOnClickListener {
            startActivity(Intent(context,SearchActivity::class.java))
        }





        return view
    }

    private fun getdata() {
        val query =  FirebaseDatabase.getInstance().getReference("Usersrooms").child(uid.toString()).orderByChild("timestamp")
        query.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                if (snapshot.exists()){
                    materialNeonProgressBar.visibility = View.GONE
                    for (datasnap in snapshot.children){
                        val snap = datasnap.getValue(MichClass::class.java)
                        list.add(snap!!)
                    }
                    adapter.submitList(list)
                    recyclerView.adapter = adapter
                } else {
                    materialNeonProgressBar.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun nextActivity(roomid: String, roomname:String) {
        if (myurl != "nul"){
                            if (michvalue != "null"){

                                val intent:Intent = Intent(Intent(context,GameActivity::class.java))
                                intent.putExtra("urlpart",urlpart)
                                intent.putExtra("url",myurl)
                                intent.putExtra("roomname",roomname)
                                intent.putExtra("mich",michvalue)
                                intent.putExtra("vid",roomid)
                                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                startActivity(intent)
                            } else {
                                val intent = Intent(Intent(context,GameActivity::class.java))
                                intent.putExtra("urlpart",urlpart)
                                intent.putExtra("url",myurl)
                                intent.putExtra("roomname",roomname)
                                intent.putExtra("mich","null")
                                intent.putExtra("vid",roomid)
                                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                startActivity(intent)
                            }

                        } else{
                            Toast.makeText(context,"you don't have avatar yet",Toast.LENGTH_LONG).show()

                        }



                }











/* FirebaseFirestore.getInstance().collection("Blockaccounts").document(roomid.toString()).collection("accounts").document(uid.toString()).get().addOnCompleteListener { firstsnap ->
            if (firstsnap.result.exists()){
                Toast.makeText(context,"this action cannot be performed",Toast.LENGTH_LONG).show()
            } else {
                FirebaseFirestore.getInstance().collection("Blockaccounts").document(uid.toString()).collection("accounts").document(roomid.toString()).get().addOnCompleteListener { secondsnp->
                    if (secondsnp.result.exists()){
                        Toast.makeText(context,"First unblock this account",Toast.LENGTH_LONG).show()
                    } else {

                    }*/





    /*   private  fun getData() :Flow<DataSnapshot> = callbackFlow {
                     val query:Query
                     query =  FirebaseDatabase.getInstance().getReference("Usersrooms").child(uid.toString()).orderByChild("timestamp").limitToFirst(15)

                    val listener = object:ValueEventListener{
                          override fun onDataChange(snapshot: DataSnapshot) {
                              list.clear()
                              if (snapshot.exists()){
                                  trySend(snapshot)
                              } else {
                                  materialNeonProgressBar.visibility = View.GONE
                              }

                          }

                          override fun onCancelled(error: DatabaseError) {
                              cancel()
                              return
                          }

                      }

                      query.addValueEventListener(listener)
                      awaitClose { query.removeEventListener(listener) }



            }*/






    override fun getrac(roomid: String, username: String, profilepic: String) {
          showdialog(roomid,username,profilepic)
    }

    private fun showdialog(roomid: String, username: String, profilepic: String) {
        val dialog : Dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.lclickdialog)
        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val block :TextView = dialog.findViewById(R.id.block)
        val delete:TextView = dialog.findViewById(R.id.delete)
        val progressbar:ProgressBar = dialog.findViewById(R.id.progres_u)
        val gg = FirebaseFirestore.getInstance().collection("Blockaccounts").document(uid.toString()).collection("accounts").document(roomid.toString()).addSnapshotListener { value, error ->
            if (value != null) {
                if (value.exists()){
                    block.setText("Unblock Account")
                } else {
                    block.setText("Block Account")
                }


            }
        }

        block.setOnClickListener {
            if (block.text == "Block Account") {
                val map: HashMap<String, Any> = hashMapOf()
                map.put("block",true)
                val data = hashMapOf("block" to true, "timestamp" to FieldValue.serverTimestamp(), "profilepic" to profilepic.toString(),"Username" to username)
                FirebaseFirestore.getInstance().collection("Blockaccounts").document(uid.toString())
                    .collection("accounts").document(roomid.toString()).set(data)
                    .addOnSuccessListener {
                        Toast.makeText(context,"Blocked", Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                    }
            } else {
                val map: HashMap<String, Any> = hashMapOf()
                map.put("block",false)
                FirebaseFirestore.getInstance().collection("Blockaccounts").document(uid.toString())
                    .collection("accounts").document(roomid.toString()).delete().addOnSuccessListener {
                        Toast.makeText(context,"Unblocked", Toast.LENGTH_LONG).show()
                        dialog.dismiss()

                    }
            }



        }

        delete.setOnClickListener {
            progressbar.visibility = View.VISIBLE
            val sender = roomid + uid
            val reciver = uid + roomid
            FirebaseDatabase.getInstance().getReference("Message").child(sender).removeValue().addOnCompleteListener {
                FirebaseDatabase.getInstance().getReference("Message2").child(reciver).removeValue().addOnSuccessListener {
                    val negativetime1 = -1699599203757
                    val negativetime2 = -1699600353986
                    val p1 = "Do not use abusive language during mich, we don't support any type of vulgarity in app, you can inform us if you faces any problem during mich , our customer support is 24 x 7"
                    val p2 = "Use emojis from chatbox to animate the avatar, Animated avatar can make  more real and fun chat between user, we are adding  more emojis in upcoming updates "
                    val p3 = "Do not use abusive language during mich, we don't support any type of vulgarity in app, you can inform us if you faces any problem during mich ," +
                            "this side is used to spawn your partner avatar, and other side for your own avatar, you can inform us if you faces any problem during mich, more features are coming soon"
                    val vp = MessageClass(uid,p1,negativetime1)
                    val sp = MessageClass(uid,p2,negativetime2)
                    FirebaseDatabase.getInstance().getReference("Message").child(sender).push().setValue(vp)
                    FirebaseDatabase.getInstance().getReference("Message").child(sender).push().setValue(sp).addOnSuccessListener {
                        val bp =  FirebaseDatabase.getInstance().getReference("Timestamp").child(uid.toString())
                        val postValues: MutableMap<String, Any> = hashMapOf()
                        postValues.put("timestamp", ServerValue.TIMESTAMP)
                        bp.updateChildren(postValues).addOnSuccessListener {
                            bp.addListenerForSingleValueEvent(object: ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val time = snapshot.child("timestamp").value as Long
                                    val negativetime3 = -1 * time
                                    val np = MessageClass(uid,p3,negativetime3)
                                    FirebaseDatabase.getInstance().getReference("Message2").child(reciver).push().setValue(np).addOnSuccessListener {
                                        FirebaseDatabase.getInstance().getReference("Usersrooms").child(uid.toString()).child(roomid.toString()).removeValue().addOnSuccessListener {
                                            progressbar.visibility = View.GONE
                                            Toast.makeText(context,"Deleted Successfully", Toast.LENGTH_LONG).show()
                                            dialog.dismiss()
                                        }
                                    }


                                }
                                override fun onCancelled(error: DatabaseError) {
                                }


                            })
                    }










                    }





                }

            }


        }










    }


}












/*      recyclerView.addOnScrollListener(object:RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling = true
                }

            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                currentitems= lm.childCount
                tottalitems= lm.itemCount
                scrolledoutitems= lm.findFirstVisibleItemPosition()
                if (isScrolling && currentitems + scrolledoutitems == tottalitems){
                    isScrolling = false
                  //  FetchData()
                  //  chip1.visibility = View.VISIBLE
                }



            }
        })*/
























