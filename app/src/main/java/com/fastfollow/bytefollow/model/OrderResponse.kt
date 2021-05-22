package com.fastfollow.bytefollow.model

data class OrderResponse(
    val status : String,
    val orders : ArrayList<OrderModel>
)