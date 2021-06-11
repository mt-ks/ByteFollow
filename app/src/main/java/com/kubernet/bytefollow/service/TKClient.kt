package com.kubernet.bytefollow.service

import android.content.Context
import com.kubernet.bytefollow.storage.UserStorage
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class TKClient (private val context: Context) {
    private val BASE_URL = "https://www.tiktok.com/"
    var dynamicCookie :String? = null


    public fun getClient(): Retrofit {

        val storage = UserStorage(context)
        val useCookie = dynamicCookie?:storage.cookie

        val httpClient = OkHttpClient.Builder()
        httpClient.readTimeout(60, TimeUnit.SECONDS)
        val gson = GsonBuilder().setLenient().create()

        httpClient.interceptors().add(Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Referer","https://www.tiktok.com/")
                .addHeader("Cookie",useCookie)
            chain.proceed(request.build())
        })

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient.build())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    fun setDynamicCookie(cookieString : String) : TKClient
    {
        dynamicCookie = cookieString
        return this
    }

}