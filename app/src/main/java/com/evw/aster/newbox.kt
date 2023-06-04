package com.evw.aster

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.Window
import com.google.android.material.button.MaterialButton

class newbox {

    var mContext: Context? = null
    fun shoDialog(context: Context?) {
        mContext = context
        val dialog = mContext?.let { Dialog(it) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.exceptionobox)
        val dialogbutton: MaterialButton = dialog?.findViewById(R.id.tim) as MaterialButton
        dialogbutton.setOnClickListener {
            dialog.dismiss()
            val menu = Intent(mContext, android.R.menu::class.java)
            mContext!!.startActivity(menu)
            (mContext as Activity).finish()
        }
        dialog.show()
    }




}

