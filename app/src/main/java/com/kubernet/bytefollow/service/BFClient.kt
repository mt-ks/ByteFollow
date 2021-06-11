package com.kubernet.bytefollow.service

import android.content.Context
import android.os.Build
import com.kubernet.bytefollow.BuildConfig
import com.kubernet.bytefollow.storage.UserStorage
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class BFClient(private val context: Context) {
    private val BASE_URL = "https://bytefollower.com/api/"
    private var userStorage: UserStorage = UserStorage(context)
    private val TAG = "BFClient"
    fun getClient() : Retrofit {
        val httpClient = OkHttpClient.Builder()
        httpClient.readTimeout(60,TimeUnit.SECONDS)
        val gson = GsonBuilder().setLenient().create()
        val userID = userStorage.userId
        httpClient.interceptors().add(Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Accept", "application/json")
                .addHeader("device_id", userStorage.deviceId)
                .addHeader("device_level", Build.VERSION.RELEASE)
                .addHeader("app_version", BuildConfig.VERSION_CODE.toString())
                .addHeader("device_name", Build.BRAND + " - " + Build.MODEL)
                .addHeader("ig_id", userID)
                .addHeader("accept-language", userStorage.language)
                .addHeader("locale-country", userStorage.language)
            chain.proceed(request.build())
        })
        httpClient.addInterceptor(InterceptorEnc())
        httpClient.addInterceptor(InterceptorDec())

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient.build())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

}