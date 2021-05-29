package com.fastfollow.bytefollow.model

data class MyOrderDetail(
    val id : Int,
    val type : Int,
    val link : String,
    val display_url : String,
    val order_insta_id : String,
    val credit : Int,
    val is_published : Int,
    val success : String
)
