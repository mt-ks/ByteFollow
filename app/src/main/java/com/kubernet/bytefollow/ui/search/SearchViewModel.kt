package com.kubernet.bytefollow.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kubernet.bytefollow.model.SearchItemsModel

class SearchViewModel : ViewModel() {

    val searchItemsModel : MutableLiveData<SearchItemsModel> by lazy{
        MutableLiveData<SearchItemsModel>()
    }

    val searchInput : MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }



}