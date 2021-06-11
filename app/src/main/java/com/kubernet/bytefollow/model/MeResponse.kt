package com.kubernet.bytefollow.model

data class MeResponse(
    val id : Int,
    val ig_id : String,
    val device_id : String,
    val gender : Int,
    val country : String,
    val device_name : String,
    val device_level : String,
    val ref_code : String,
    val app_version : String,
    val credit : Int,
    val in_progress_order : Int
)