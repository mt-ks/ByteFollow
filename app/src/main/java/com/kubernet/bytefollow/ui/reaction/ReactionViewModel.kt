package com.kubernet.bytefollow.ui.reaction

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kubernet.bytefollow.model.OrderModel

class ReactionViewModel : ViewModel() {

    val orderModel : MutableLiveData<ArrayList<OrderModel>> by lazy{
        MutableLiveData<ArrayList<OrderModel>>()
    }

    val actionErrorCount : MutableLiveData<Int> by lazy{
        MutableLiveData<Int>()
    }

    val waitOrderTime : MutableLiveData<Int> by lazy{
        MutableLiveData<Int>()
    }

    val allActionDelaySecond : MutableLiveData<Int> by lazy{
        MutableLiveData<Int>()
    }

    val reactionDisplayUrl : MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val reactionUsername : MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val reactionStatus : MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val lastStatus : MutableLiveData<String> by lazy{
        MutableLiveData<String>()
    }

}