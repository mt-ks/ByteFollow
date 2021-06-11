package com.kubernet.bytefollow.dialogs

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import android.widget.Toast
import com.kubernet.bytefollow.R
import com.kubernet.bytefollow.databinding.DialogReferenceBinding
import com.kubernet.bytefollow.service.BFApi
import com.kubernet.bytefollow.service.BFClient
import com.kubernet.bytefollow.service.BFErrorHandler
import com.kubernet.bytefollow.ui.reference.ReferenceViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class ReferenceDialog(
    private val activity: Activity,
    private val referenceViewModel: ReferenceViewModel
) {
    private var _binding : DialogReferenceBinding? = null
    private val binding get() = _binding!!
    private lateinit var dialog: Dialog
    private val loadingDialog: LoadingDialog = LoadingDialog(activity)
    private var compositeDisposable : CompositeDisposable? = null

    fun start()
    {
        _binding = DialogReferenceBinding.inflate(LayoutInflater.from(activity))
        dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        dialog.setContentView(binding.root)
        dialog.show()
        compositeDisposable = CompositeDisposable()
        binding.confirmRefCode.setOnClickListener { confirmRefCode() }


    }

    private fun confirmRefCode()
    {
        val code = binding.refCode.text.toString().trim()
        if (code.isEmpty() || code.isBlank())
        {
            Toast.makeText(activity, activity.getString(R.string.incorrect_ref_code), Toast.LENGTH_SHORT).show()
            return
        }
        loadingDialog.start()
        val api = (BFClient(activity)).getClient().create(BFApi::class.java)
        compositeDisposable?.add(api.setReference(code).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                referenceViewModel.canRef.value = false
                loadingDialog.stop()
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
            },{
                val error = BFErrorHandler(activity,it)
                Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
                loadingDialog.stop()
            }))

    }


    fun stop()
    {
        compositeDisposable?.clear()
        dialog.cancel()
    }

}