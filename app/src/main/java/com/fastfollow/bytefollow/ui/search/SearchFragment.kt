package com.fastfollow.bytefollow.ui.search

import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fastfollow.bytefollow.adapters.SearchAdapter
import com.fastfollow.bytefollow.databinding.FragmentSearchBinding
import com.fastfollow.bytefollow.dialogs.LoadingDialog
import com.fastfollow.bytefollow.dialogs.NewOrderDialog
import com.fastfollow.bytefollow.helpers.UserRequireChecker
import com.fastfollow.bytefollow.model.SearchItemsModel
import com.fastfollow.bytefollow.model.VideoDetail
import com.fastfollow.bytefollow.service.TKApi
import com.fastfollow.bytefollow.service.TKClient
import com.fastfollow.bytefollow.storage.UserStorage
import com.fastfollow.bytefollow.ui.profile.ProfileViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.HttpException

class SearchFragment : SearchAdapter.OnVideoClickListener,Fragment() {

    private val TAG = "SearchFragment"
    private lateinit var loadingDialog: LoadingDialog
    private var _binding : FragmentSearchBinding? = null
    private var compositeDisposable : CompositeDisposable? = null
    private lateinit var userStorage : UserStorage
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private val viewModel : SearchViewModel by activityViewModels()
    private val profileViewModel : ProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSearchBinding.inflate(inflater,container,false)
        compositeDisposable = CompositeDisposable()
        userStorage = UserStorage(requireContext())
        loadingDialog = LoadingDialog(requireActivity())

        binding.searchButton.setOnClickListener {
            getUserItems(binding.searchInput.text.toString().trim())
        }

        viewModel.searchItemsModel.observe(viewLifecycleOwner,{
            val adapter = SearchAdapter(requireContext(), it.items,it.userDetail,this)
            recyclerView = binding.searchRecycler
            val gridLayoutManager = GridLayoutManager(requireContext(),3)
            recyclerView.layoutManager = gridLayoutManager
            gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup(){
                override fun getSpanSize(position: Int): Int {
                    return if (position == 0) 3 else 1
                }
            }
            recyclerView.adapter = adapter
        })

        if (viewModel.searchItemsModel.value == null)
        {
            val username : String = userStorage.username
            getUserItems(username)
        }

        return _binding!!.root
    }

    private fun getUserItems(username : String)
    {
        loadingDialog.start()
        val api = (TKClient(requireActivity())).getClient().create(TKApi::class.java)
        compositeDisposable?.add(api.getUserInfo(username).subscribeOn(Schedulers.io()).observeOn(
            AndroidSchedulers.mainThread())
            .subscribe({
                loadingDialog.stop()
                handleRecycler(it)
            },{
                loadingDialog.stop()
                errorHandler(it)
            }))
    }

    private fun errorHandler(it : Throwable)
    {
        if (it is HttpException)
        {
            if (it.code() == 404)
            {
                Toast.makeText(requireContext(),"Kullanıcı bulunamadı!",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(requireContext(),"Bir sorun oluştu!",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleRecycler(it : ResponseBody)
    {
        val check  = UserRequireChecker(it)
        val checkItems : Boolean = check.checkItems()
        val checkUser : Boolean  = check.checkUser()
        if (checkItems && checkUser)
        {
            viewModel.searchItemsModel.value = SearchItemsModel(check.itemList,check.userDetail)
        }
    }

    override fun onVideoClick(videoDetail: VideoDetail) {
        val link : String = "https://www.tiktok.com/@${videoDetail.author.uniqueId}/${videoDetail.id}"
        val displayUrl : String = videoDetail.video.cover
        val orderId : String = videoDetail.id
        val orderDialog : NewOrderDialog = NewOrderDialog(requireActivity(),viewModel,profileViewModel,link,displayUrl,orderId,2)
        orderDialog.show()
    }

}