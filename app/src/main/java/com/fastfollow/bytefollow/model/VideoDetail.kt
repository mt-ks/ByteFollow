package com.fastfollow.bytefollow.model

data class VideoDetail (
    val id : String,
    val video : VideoModel,
    var author : UserInfo,
    var stats : VideoStats,
    var digged: Boolean
)