package com.kubernet.bytefollow.model

data class OrderModel (
    val id : Int,
    val client_id : Int,
    val order_id : Int,
    val is_completed : Int,
    val created_at : String,
    val updated_at : String,
    val order : OrderDetailModel
)