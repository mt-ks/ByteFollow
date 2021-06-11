package com.kubernet.bytefollow.ui.orders

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kubernet.bytefollow.model.MyOrderDetail

class OrdersViewModel : ViewModel() {

    val myOrders : MutableLiveData<List<MyOrderDetail>> by lazy{
        MutableLiveData<List<MyOrderDetail>>()
    }

}