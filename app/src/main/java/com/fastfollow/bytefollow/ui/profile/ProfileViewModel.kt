package com.fastfollow.bytefollow.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fastfollow.bytefollow.model.UserDetail

class ProfileViewModel : ViewModel() {

    val currentCredit : MutableLiveData<Int> by lazy{
        MutableLiveData<Int>()
    }

    val userDetail : MutableLiveData<UserDetail> by lazy {
        MutableLiveData<UserDetail>()
    }



}