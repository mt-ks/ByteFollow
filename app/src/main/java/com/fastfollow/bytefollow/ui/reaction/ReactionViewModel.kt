package com.fastfollow.bytefollow.ui.reaction

import android.webkit.WebView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fastfollow.bytefollow.model.OrderModel
import com.fastfollow.bytefollow.model.OrderResponse

class ReactionViewModel : ViewModel() {

    val orderModel : MutableLiveData<ArrayList<OrderModel>> by lazy{
        MutableLiveData<ArrayList<OrderModel>>()
    }

    val waitOrderTime : MutableLiveData<Int> by lazy{
        MutableLiveData<Int>()
    }

}