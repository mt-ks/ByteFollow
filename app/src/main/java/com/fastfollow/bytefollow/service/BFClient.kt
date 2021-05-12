package com.fastfollow.bytefollow.service

import android.content.Context
import android.os.Build
import com.fastfollow.bytefollow.BuildConfig
import com.fastfollow.bytefollow.storage.UserStorage
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class BFClient(private val context: Context) {
    private val BASE_URL = "http://192.168.3.10:8000/api/"
    private var userStorage: UserStorage = UserStorage(context)

    fun getClient() : Retrofit {
        val httpClient = OkHttpClient.Builder()
        httpClient.readTimeout(60,TimeUnit.SECONDS)
        val gson = GsonBuilder().setLenient().create()

        httpClient.interceptors().add(Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Accept", "application/json")
                .addHeader("device_id", userStorage.device_id)
                .addHeader("device_level", Build.VERSION.RELEASE)
                .addHeader("app_version", BuildConfig.VERSION_CODE.toString())
                .addHeader("device_name", Build.BRAND + " - " + Build.MODEL)
                .addHeader("ig_id", userStorage.user_id)
                .addHeader("accept-language", userStorage.language)
                .addHeader("locale-country", userStorage.country)
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