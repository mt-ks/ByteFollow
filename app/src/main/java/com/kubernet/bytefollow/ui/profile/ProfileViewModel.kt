package com.kubernet.bytefollow.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kubernet.bytefollow.model.UserDetail

class ProfileViewModel : ViewModel() {

    val currentCredit : MutableLiveData<Int> by lazy{
        MutableLiveData<Int>()
    }

    val userDetail : MutableLiveData<UserDetail> by lazy {
        MutableLiveData<UserDetail>()
    }



}