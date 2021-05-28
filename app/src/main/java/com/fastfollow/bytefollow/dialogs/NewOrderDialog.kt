package com.fastfollow.bytefollow.dialogs

import android.app.Activity
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.widget.ImageView
import android.widget.SeekBar
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.fastfollow.bytefollow.R
import com.fastfollow.bytefollow.databinding.DialogNewOrderBinding
import com.fastfollow.bytefollow.databinding.FragmentSearchBinding
import com.fastfollow.bytefollow.storage.UserStorage
import com.fastfollow.bytefollow.ui.profile.ProfileViewModel
import com.fastfollow.bytefollow.ui.search.SearchViewModel

class NewOrderDialog (private var activity : Activity,
                      private var searchViewModel: SearchViewModel,
                      private var profileViewModel: ProfileViewModel,
                      private val link : String,
                      private val displayUrl : String,
                      private val orderId : String,
                      private var type : Int
)
{

    private val userStorage : UserStorage = UserStorage(activity)
    private val loadingDialog : LoadingDialog = LoadingDialog(activity)
    private lateinit var dialog: Dialog
    private var _binding : DialogNewOrderBinding? = null
    private val binding get() = _binding!!


    fun show() {
        _binding = DialogNewOrderBinding.inflate(LayoutInflater.from(activity))

        dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.setCancelable(true)
        dialog.setContentView(binding.root)
        dialog.show()

        Glide.with(activity).load(displayUrl).into(binding.selectedPhoto)
        binding.closeDialog.setOnClickListener{ stop() }

        var currentCredit = userStorage.credit
        val maxLimitation = 5000

        if (currentCredit > maxLimitation)
            currentCredit = maxLimitation

        binding.maximum.text = "Maximum: " + (currentCredit / 4)
        binding.seekBar.max = currentCredit / 4

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                binding.seekBarCountViewer.text = p1.toString()
                binding.amountCoin.text = (p1 * 4).toString()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })

    }

    private fun stop()
    {
        dialog.dismiss()
    }

}