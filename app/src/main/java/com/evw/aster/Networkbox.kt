package com.evw.aster

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.Window
import com.google.android.material.button.MaterialButton

class Networkbox {

    fun shownetworkdialog(context: Context?) {
        val dialog = context?.let { Dialog(it) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.networkbox)
        val dialogbutton: MaterialButton = dialog?.findViewById(R.id.tim) as MaterialButton
        dialogbutton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

}