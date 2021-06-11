package com.kubernet.bytefollow.model

data class MyOrdersResponse(
    val status : String,
    val ordered_list : List<MyOrderDetail>
)
