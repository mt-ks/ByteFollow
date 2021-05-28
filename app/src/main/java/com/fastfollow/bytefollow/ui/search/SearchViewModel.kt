package com.fastfollow.bytefollow.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fastfollow.bytefollow.model.SearchItemsModel
import com.fastfollow.bytefollow.model.UserDetail
import com.fastfollow.bytefollow.model.VideoDetail

class SearchViewModel : ViewModel() {

    val searchItemsModel : MutableLiveData<SearchItemsModel> by lazy{
        MutableLiveData<SearchItemsModel>()
    }

    val searchInput : MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }



}