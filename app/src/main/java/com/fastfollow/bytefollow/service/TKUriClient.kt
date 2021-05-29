package com.fastfollow.bytefollow.service

import com.fastfollow.bytefollow.storage.UserStorage
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class TKUriClient {
    private val baseUrl = "http://vm.tiktok.com/"

    public fun getClient(): Retrofit {


        val httpClient = OkHttpClient.Builder()
        httpClient.readTimeout(60, TimeUnit.SECONDS)
        val gson = GsonBuilder().setLenient().create()

        httpClient.followRedirects(false)

        httpClient.interceptors().add(Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Referer","https://www.tiktok.com/")
            chain.proceed(request.build())
        })

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient.build())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

}