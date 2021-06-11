package com.kubernet.bytefollow.model

data class OrderDetailModel (
    val id : Int,
    val type: Int,
    val link : String,
    val credit : Int,
    val created_at : String,
    val updated_at : String
)