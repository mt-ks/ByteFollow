package com.kubernet.bytefollow.model

/**
 * name field works only error responses.
 * example : {"data":{"description":"session expired, please sign in again","name":"session_expired"},"message":"error"}
 */
data class UserData(
    val user_id : String,
    val user_id_str: String,
    val sec_user_id: String,
    val screen_name: String,
    val avatar_url : String,
    val description: String,
    val mobile: String,
    val email : String,
    val username: String,
    val has_password: Int,
    val name : String
)