package com.kubernet.bytefollow.dialogs

import android.app.Activity
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.kubernet.bytefollow.R

class LoadingDialog(private val activity : Activity) {
    private lateinit var dialog : Dialog
    private var isStarted = false
    fun start()
    {
        if (!isStarted)
        {
            isStarted = true
            dialog = Dialog(activity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_loading)
            dialog.show()
        }
    }

    fun stop()
    {
        isStarted = false
        dialog.cancel()
    }

}