package com.evw.aster

import android.app.Activity
import android.app.Dialog
import android.view.Window
import android.widget.TextView

class ImageDialog {

    fun vis(activity: Activity?) {
        val dialog = activity?.let { Dialog(it) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(true)
        dialog?.setContentView(R.layout.imageviewnow)
        val dialogtext: TextView = dialog?.findViewById(R.id.tim) as TextView
        dialogtext.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

}