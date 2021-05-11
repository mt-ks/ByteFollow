package com.fastfollow.bytefollow.model

data class UserInfo(
    val id : String,
    val uniqueId : String,
    val avatarMedium: String,
    val avatarLarger: String,
    val avatarThumb: String,
    val relation : Int,
    val privateAccount: Boolean,
    val secret : Boolean,
    val secUid: String
)