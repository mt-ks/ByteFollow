package com.fastfollow.bytefollow.ui.orders

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fastfollow.bytefollow.model.MyOrderDetail

class OrdersViewModel : ViewModel() {

    val myOrders : MutableLiveData<List<MyOrderDetail>> by lazy{
        MutableLiveData<List<MyOrderDetail>>()
    }

}