package com.fastfollow.bytefollow.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.Window
import com.fastfollow.bytefollow.databinding.DialogNeedUpgradeBinding


class UpdateDialog(
    private val activity: Activity,
    private val route : String
) {
    private var _binding : DialogNeedUpgradeBinding? = null
    private val binding get() = _binding!!
    private lateinit var dialog: Dialog

    fun start()
    {
        _binding = DialogNeedUpgradeBinding.inflate(LayoutInflater.from(activity))
        dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(binding.root)
        dialog.show()
        binding.upgrade.setOnClickListener {
            updateHandler()
        }
    }

    private fun updateHandler()
    {
        try{
            activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(route)))
        }catch (e : Exception){}
    }

    fun stop()
    {
        dialog.cancel()
    }

}