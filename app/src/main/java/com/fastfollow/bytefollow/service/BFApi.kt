package com.fastfollow.bytefollow.service

import com.fastfollow.bytefollow.model.GenericResponse
import com.fastfollow.bytefollow.model.MeResponse
import com.fastfollow.bytefollow.model.RegisterDeviceModel
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface BFApi {
    @FormUrlEncoded
    @POST("register")
    fun register(@Field("username") username : String) : Observable<RegisterDeviceModel>

    @GET("me")
    fun me() : Observable<MeResponse>


}