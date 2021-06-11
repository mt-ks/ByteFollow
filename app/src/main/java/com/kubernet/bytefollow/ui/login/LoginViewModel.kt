package com.kubernet.bytefollow.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    var number = 0;

    val currentNumber : MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    val currentBoolean : MutableLiveData<Boolean> by lazy{
        MutableLiveData<Boolean>()
    }

}