package com.fastfollow.bytefollow.model

data class OrderResponse(
    val status : String,
    val orders : ArrayList<OrderModel>,
    val follow_inject : String,
    val like_inject : String
)