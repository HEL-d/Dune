package com.evw.aster

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.Window
import com.google.android.material.button.MaterialButton

class profileeditnow {

    var mContext: Context? = null
    fun givedilaog(context: Context?,username:String?,phoneNumber:String?) {
        mContext = context
        val dialog = mContext?.let { Dialog(it) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.profileedit)
        val dialogbutton1: MaterialButton = dialog?.findViewById(R.id.editavatar_okay) as MaterialButton
        val dialogbutton2: MaterialButton = dialog.findViewById(R.id.editdetails_okay) as MaterialButton
        val dialogbutton3: MaterialButton = dialog.findViewById(R.id.cancel_okay) as MaterialButton
        dialogbutton1.setOnClickListener {
            dialog.dismiss()
                val intent: Intent = Intent(context,avatargen::class.java)
                 intent.putExtra("avatarbool","true")
                intent.putExtra("username",username)
                intent.putExtra("phonenumber",phoneNumber)
                 context?.startActivity(intent)
        }

        dialogbutton2.setOnClickListener {
            dialog.dismiss()
            context?.startActivity(Intent(context,EditActivity::class.java))
        }

        dialogbutton3.setOnClickListener {
            dialog.dismiss()
        }


        dialog.show()
    }







}

