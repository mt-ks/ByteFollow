package com.fastfollow.bytefollow.dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.fastfollow.bytefollow.R
import com.fastfollow.bytefollow.databinding.DialogNewOrderBinding
import com.fastfollow.bytefollow.databinding.FragmentSearchBinding
import com.fastfollow.bytefollow.service.BFApi
import com.fastfollow.bytefollow.service.BFClient
import com.fastfollow.bytefollow.service.BFErrorHandler
import com.fastfollow.bytefollow.storage.UserStorage
import com.fastfollow.bytefollow.ui.profile.ProfileViewModel
import com.fastfollow.bytefollow.ui.search.SearchViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

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
    private var compositeDisposable : CompositeDisposable? = null


    @SuppressLint("SetTextI18n")
    fun show() {
        Log.d("ORDER","Link $link")
        _binding = DialogNewOrderBinding.inflate(LayoutInflater.from(activity))
        compositeDisposable = CompositeDisposable()
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

        binding.maximum.text = String.format(activity.getString(R.string.maximum_x),(currentCredit / 4).toString())
        binding.seekBar.max = currentCredit / 4

        binding.actionType.text = if (type == 1) activity.getString(R.string.followers) else activity.getString(R.string.likes)

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
        binding.addOrder.setOnClickListener {
            addOrder()
        }

    }

    private fun addOrder()
    {
        val quantity = binding.seekBar.progress
        if (quantity < 10){
            toast(activity.getString(R.string.quantity_limit));
            return
        }

        loadingDialog.start()
        val api = (BFClient(activity)).getClient().create(BFApi::class.java)
        compositeDisposable?.add(api.newOrder(link,displayUrl,orderId,quantity,type).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                toast(it.message)
                profileViewModel.currentCredit.value = it.client
                loadingDialog.stop()
                stop()
            },{
                loadingDialog.stop()
                if(it is HttpException)
                {
                    val error = (BFErrorHandler(activity,it))
                    toast(error.message)
                }else{
                    toast(activity.getString(R.string.an_error_occurred))
                }
            }))

    }

    private fun toast(message : String)
    {
        Toast.makeText(activity,message,Toast.LENGTH_SHORT).show()
    }

    private fun stop()
    {
        compositeDisposable?.clear()
        dialog.dismiss()
    }

}