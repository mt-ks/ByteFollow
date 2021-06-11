package com.kubernet.bytefollow.ui.promotion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.kubernet.bytefollow.R
import com.kubernet.bytefollow.databinding.FragmentPromotionBinding
import com.kubernet.bytefollow.dialogs.LoadingDialog
import com.kubernet.bytefollow.service.BFApi
import com.kubernet.bytefollow.service.BFClient
import com.kubernet.bytefollow.service.BFErrorHandler
import com.kubernet.bytefollow.ui.profile.ProfileViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class PromotionFragment : Fragment() {

    private var _binding : FragmentPromotionBinding? = null
    private val binding get() = _binding!!
    private var compositeDisposable : CompositeDisposable? = null
    private val profileViewModel : ProfileViewModel by activityViewModels()
    private lateinit var loadingDialog: LoadingDialog


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPromotionBinding.inflate(inflater,container,false)
        binding.confirmPromotion.setOnClickListener { checkPromoCode() }
        compositeDisposable = CompositeDisposable()
        loadingDialog = LoadingDialog(requireActivity())
        return _binding!!.root
    }

    private fun checkPromoCode()
    {
        val code = binding.promotionCode.text.toString().trim()
        if (code.isBlank() || code.isEmpty())
        {
            Toast.makeText(requireContext(), getString(R.string.incorrect_promotion_code), Toast.LENGTH_SHORT).show()
            return
        }

        loadingDialog.start()
        val api = (BFClient(requireContext())).getClient().create(BFApi::class.java)
        compositeDisposable?.add(api.confirmPromotion(code).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                profileViewModel.currentCredit.value = it.credit
                loadingDialog.stop()
            },{
                it.printStackTrace()
                val error = BFErrorHandler(requireActivity(),it)
                loadingDialog.stop()
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
            }))

    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable?.clear()
    }

}