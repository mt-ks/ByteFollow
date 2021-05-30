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
import com.fastfollow.bytefollow.R
import com.fastfollow.bytefollow.adapters.SearchAdapter
import com.fastfollow.bytefollow.databinding.FragmentSearchBinding
import com.fastfollow.bytefollow.dialogs.LoadingDialog
import com.fastfollow.bytefollow.dialogs.NewOrderDialog
import com.fastfollow.bytefollow.helpers.URIControl
import com.fastfollow.bytefollow.helpers.UserRequireChecker
import com.fastfollow.bytefollow.model.OembedResponse
import com.fastfollow.bytefollow.model.SearchItemsModel
import com.fastfollow.bytefollow.model.UserDetail
import com.fastfollow.bytefollow.model.VideoDetail
import com.fastfollow.bytefollow.service.TKApi
import com.fastfollow.bytefollow.service.TKClient
import com.fastfollow.bytefollow.service.TKUriApi
import com.fastfollow.bytefollow.service.TKUriClient
import com.fastfollow.bytefollow.storage.UserStorage
import com.fastfollow.bytefollow.ui.profile.ProfileViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

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
            searchHandler()
        }

        viewModel.searchItemsModel.observe(viewLifecycleOwner,{
            val adapter = SearchAdapter(requireContext(), it.items,it.userDetail,this,this)
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

    private fun searchHandler()
    {
        val searchQuery : String = binding.searchInput.text.toString().trim()
        if (searchQuery.contains("vm.tiktok.com/"))
        {
            val shortCode : String? = URIControl(searchQuery).parseShortenCode()
            if (shortCode == null)
            {
                Toast.makeText(requireContext(), getString(R.string.incorrect_link), Toast.LENGTH_SHORT).show()
                return
            }
            bringLongUrl(shortCode)
        }else{
            getUserItems(searchQuery)
        }
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
                Toast.makeText(requireContext(),getString(R.string.user_not_fund),Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(requireContext(),getString(R.string.an_error_occurred),Toast.LENGTH_SHORT).show()
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
            if(check.userDetail.user.privateAccount)
            {
                Toast.makeText(requireContext(),getString(R.string.private_profile),Toast.LENGTH_SHORT).show()
            }else{
                viewModel.searchItemsModel.value = SearchItemsModel(check.itemList,check.userDetail)
            }
        }
    }

    override fun onVideoClick(videoDetail: VideoDetail) {
        val link : String = "https://www.tiktok.com/@${videoDetail.author.uniqueId}/video/${videoDetail.id}"
        val displayUrl : String = videoDetail.video.cover
        val orderId : String = videoDetail.id
        val orderDialog : NewOrderDialog = NewOrderDialog(requireActivity(),viewModel,profileViewModel,link,displayUrl,orderId,2)
        orderDialog.show()
    }

    override fun onProfileClick(userDetail: UserDetail) {
        val link : String = "https://www.tiktok.com/@${userDetail.user.uniqueId}"
        val displayUrl : String = userDetail.user.avatarMedium
        val orderId : String = userDetail.user.id
        val orderDialog : NewOrderDialog = NewOrderDialog(requireActivity(),viewModel,profileViewModel,link,displayUrl,orderId,1)
        orderDialog.show()
    }


    private fun bringLongUrl(shortCode : String)
    {
        loadingDialog.start()
        val api = TKUriClient().getClient().create(TKUriApi::class.java)
        val call = api.getShortCode(shortCode)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                response.headers().get("Location")?.let {

                    checkProfileOrVideo(it)

                } ?:run {
                    Toast.makeText(requireContext(), getString(R.string.incorrect_link), Toast.LENGTH_SHORT).show()
                    loadingDialog.stop()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                loadingDialog.stop()
            }

        })
    }

    private fun checkProfileOrVideo(location : String)
    {
        URIControl(location).parseVideoURL()?.let {
            getOembed(location,it)
        } ?:run {
            getUserItems(URIControl(location).parseVideoUsername())
        }
    }

    private fun getOembed(url : String, videoID: String)
    {
        val api = TKClient(requireContext()).getClient().create(TKApi::class.java)
        val call = api.getOembed(url)
        call.enqueue(object : Callback<OembedResponse> {
            override fun onResponse(
                call: Call<OembedResponse>,
                response: Response<OembedResponse>
            ) {
                val username = URIControl(response.body()?.author_url?:"noname").parseVideoUsername()
                getDirectMediaDetail(username,videoID)
            }

            override fun onFailure(call: Call<OembedResponse>, t: Throwable) {
                Toast.makeText(requireContext(), getString(R.string.an_error_occurred), Toast.LENGTH_SHORT).show()
                loadingDialog.stop()
            }

        })
    }

    private fun getDirectMediaDetail(username: String, videoID: String)
    {
        val api = (TKClient(requireContext())).getClient().create(TKApi::class.java)
        compositeDisposable?.add(api.getVideoInfo(username,videoID).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val video = UserRequireChecker(it);
                if (video.checkVideo())
                {
                    onVideoClick(video.videoDetail)
                }else{
                    Toast.makeText(requireContext(), getString(R.string.post_not_found), Toast.LENGTH_SHORT).show()
                }
                loadingDialog.stop()
            },{
                it.printStackTrace()
                loadingDialog.stop()
            }))
    }


    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable?.clear()
    }
}