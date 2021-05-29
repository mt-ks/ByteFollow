package com.fastfollow.bytefollow.dialogs

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.Window
import com.fastfollow.bytefollow.MainActivity
import com.fastfollow.bytefollow.databinding.DialogLogoutBinding
import com.fastfollow.bytefollow.databinding.DialogNeedUpgradeBinding
import com.fastfollow.bytefollow.storage.UserStorage


class LogoutDialog(
    private val activity: Activity
) {
    private var _binding : DialogLogoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var dialog: Dialog
    private val userStorage : UserStorage = UserStorage(activity)

    fun start()
    {
        _binding = DialogLogoutBinding.inflate(LayoutInflater.from(activity))
        dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(binding.root)
        dialog.show()

        binding.ok.setOnClickListener { logout() }

    }

    private fun logout()
    {
        userStorage.clearDB()
        val intent : Intent = Intent(activity,MainActivity::class.java)
        stop()
        activity.startActivity(intent)
        activity.finish()
    }

    fun stop()
    {
        dialog.cancel()
    }

}