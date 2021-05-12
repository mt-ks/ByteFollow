package com.fastfollow.bytefollow.service

import com.fastfollow.bytefollow.model.GenericResponse
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface BFApi {
    @FormUrlEncoded
    @POST("register")
    fun register(@Field("username") username : String) : Observable<GenericResponse>



}