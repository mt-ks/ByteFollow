package com.fastfollow.bytefollow.service

import com.fastfollow.bytefollow.model.ResponseCheckCookie
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

interface TKApi {
    @GET("passport/web/account/info/?account_sdk_source=web&aid=1459&language=tr&is_sso=false&host=&region=")
    fun checkCookie() : Observable<ResponseCheckCookie>

    @GET("@{username}")
    fun getUserInfo(@Path("username") username : String) : Observable<ResponseBody>

    @GET("@{username}/video/{videoID}")
    fun getVideoInfo(@Path("username") username : String, @Path("videoID") videoID : String) : Observable<ResponseBody>
}