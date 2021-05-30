package com.fastfollow.bytefollow.service

import com.fastfollow.bytefollow.model.*
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

    @GET("reaction/get")
    fun newOrder() : Observable<OrderResponse>

    @FormUrlEncoded
    @POST("reaction/check")
    fun check(@Field("status") status : Int, @Field("order") order : Int) : Observable<CheckOrderResponse>

    @FormUrlEncoded
    @POST("order/new")
    fun newOrder(@Field("link") link : String,
                 @Field("display_url") displayUrl : String,
                 @Field("order_insta_id") orderInstaId : String,
                 @Field("quantity") quantity : Int,
                 @Field("type") type : Int
    ) : Observable<CheckOrderResponse>

    @GET("order/list")
    fun getOrders() : Observable<MyOrdersResponse>

    @FormUrlEncoded
    @POST("promotion/confirm")
    fun confirmPromotion(@Field("promotion_name") promotionName: String) : Observable<PromotionResponse>

    @FormUrlEncoded
    @POST("reference/set")
    fun setReference(@Field("ref_code") ref_code : String) : Observable<GenericResponse>

    @GET("reference/get")
    fun getReference() : Observable<ReferenceResponse>

}