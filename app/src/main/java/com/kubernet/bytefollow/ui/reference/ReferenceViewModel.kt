package com.kubernet.bytefollow.ui.reference

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ReferenceViewModel : ViewModel() {

    val canRef : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val totalRef : MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

}