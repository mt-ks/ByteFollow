package com.fastfollow.bytefollow.service

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface TKUriApi {

    @GET("{path}")
    fun getShortCode(@Path("path") path : String) : Call<ResponseBody>


}