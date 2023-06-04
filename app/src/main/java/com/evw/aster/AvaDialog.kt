package com.evw.aster

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.Window
import android.widget.TextView

class AvaDialog {
    fun showDialog(context: Context?, msg1: String?, msg2:String) {
        val dialog = context?.let { Dialog(it) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(true)
        dialog?.setContentView(R.layout.dialogbox)
        val text1 = dialog?.findViewById(R.id.mav) as TextView
        val text2 = dialog.findViewById(R.id.mav2) as TextView
        text1.text = msg1
        text2.text = msg2
        val dialogtext: TextView = dialog.findViewById(R.id.tim) as TextView
        dialogtext.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

}
