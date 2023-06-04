package com.evw.aster
import android.app.Activity
import android.app.Dialog
import android.view.Window


class progressdialog {
    fun shownetworkdialog(activity: Activity?) {
        val dialog = activity?.let { Dialog(it) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.progressdialog)
        dialog?.show()
    }





}