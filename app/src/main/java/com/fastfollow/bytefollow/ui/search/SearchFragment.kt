package com.fastfollow.bytefollow.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fastfollow.bytefollow.adapters.SearchAdapter
import com.fastfollow.bytefollow.databinding.FragmentSearchBinding
import com.fastfollow.bytefollow.helpers.UserRequireChecker
import com.fastfollow.bytefollow.model.SearchItemsModel
import com.fastfollow.bytefollow.service.TKApi
import com.fastfollow.bytefollow.service.TKClient
import com.fastfollow.bytefollow.storage.UserStorage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody

class SearchFragment : Fragment() {

    private val TAG = "SearchFragment"
    private var _binding : FragmentSearchBinding? = null
    private var compositeDisposable : CompositeDisposable? = null
    private lateinit var userStorage : UserStorage
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private val viewModel : SearchViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater,container,false)
        compositeDisposable = CompositeDisposable()
        userStorage = UserStorage(requireContext())

        binding.searchButton.setOnClickListener {
            getUserItems(binding.searchInput.text.toString())
        }

        viewModel.searchItemsModel.observe(viewLifecycleOwner,{
            val adapter = SearchAdapter(requireContext(), it.items,it.userDetail)
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
            getUserItems(userStorage.username)
        }

        return _binding!!.root
    }

    private fun getUserItems(username : String)
    {
        val api = (TKClient(requireActivity())).getClient().create(TKApi::class.java)
        compositeDisposable?.add(api.getUserInfo(username).subscribeOn(Schedulers.io()).observeOn(
            AndroidSchedulers.mainThread())
            .subscribe({
                handleRecycler(it)
            },{
                it.printStackTrace()
            }))
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

}