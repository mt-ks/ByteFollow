package com.fastfollow.bytefollow.ui.reference


import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.fastfollow.bytefollow.R
import com.fastfollow.bytefollow.databinding.FragmentReferenceBinding
import com.fastfollow.bytefollow.dialogs.ReferenceDialog
import com.fastfollow.bytefollow.service.BFApi
import com.fastfollow.bytefollow.service.BFClient
import com.fastfollow.bytefollow.service.BFErrorHandler
import com.fastfollow.bytefollow.storage.UserStorage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class ReferenceFragment : Fragment() {
    private var _binding : FragmentReferenceBinding? = null
    private val binding get() = _binding!!
    private var compositeDisposable : CompositeDisposable? = null
    private lateinit var userStorage : UserStorage
    private val viewModel : ReferenceViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReferenceBinding.inflate(inflater,container,false)
        compositeDisposable = CompositeDisposable()
        userStorage = UserStorage(requireContext())
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.refCode.text = userStorage.meInfo.ref_code
        binding.copyRefCode.setOnClickListener { copyRefCode() }
        binding.enterRefCode.setOnClickListener {
            (ReferenceDialog(requireActivity(),viewModel)).start()
        }

        if (viewModel.canRef.value == null) checkRefState()
        viewModel.canRef.observe(viewLifecycleOwner,{
            if (!it)
                binding.enterRefCode.visibility = View.GONE
            else
                binding.enterRefCode.visibility = View.VISIBLE
        })

        viewModel.totalRef.observe(viewLifecycleOwner,{
            binding.totalRef.visibility = View.VISIBLE
            binding.totalRef.text = String.format(getString(R.string.you_have_x_reference),it.toString())
        })

        binding.swipeRefreshReference.setOnRefreshListener {
            checkRefState()
        }



    }

    private fun checkRefState()
    {
        val api = (BFClient(requireContext())).getClient().create(BFApi::class.java)
        compositeDisposable?.add(api.getReference().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewModel.canRef.value = it.can_ref
                viewModel.totalRef.value = it.total_references
                binding.swipeRefreshReference.isRefreshing = false
            },{
                val error = BFErrorHandler(requireActivity(),it)
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                binding.swipeRefreshReference.isRefreshing = false
            }))

    }

    private fun copyRefCode()
    {
        val refCode = userStorage.meInfo.ref_code
        val clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("Reference code", refCode)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(requireContext(), getString(R.string.copied), Toast.LENGTH_SHORT).show()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable?.clear()
    }
}