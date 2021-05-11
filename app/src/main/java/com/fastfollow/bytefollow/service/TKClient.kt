package com.fastfollow.bytefollow.service

import android.content.Context
import com.fastfollow.bytefollow.storage.UserStorage
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class TKClient (val context: Context) {
    private val BASE_URL = "https://www.tiktok.com/"
    var dynamicCookie :String? = null

    fun setDynamicCookie(cookieString : String) : TKClient
    {
        dynamicCookie = cookieString
        return this
    }


    public fun getClient(): Retrofit {

        val storage = UserStorage(context)

        val useCookie = dynamicCookie?:storage.cookie

        val httpClient = OkHttpClient.Builder()
        httpClient.readTimeout(60, TimeUnit.SECONDS)

        val gson = GsonBuilder()
            .setLenient()
            .create()

        httpClient.interceptors().add(object : Interceptor{
            override fun intercept(chain: Interceptor.Chain): Response {
               val request = chain.request().newBuilder()
                   .addHeader("Referer","https://www.tiktok.com/")
                   .addHeader("Cookie",useCookie)

                return chain.proceed(request.build())
            }
        })

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient.build())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

}