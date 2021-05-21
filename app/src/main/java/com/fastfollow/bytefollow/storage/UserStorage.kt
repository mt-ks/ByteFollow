package com.fastfollow.bytefollow.storage

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.fastfollow.bytefollow.model.UserInfo
import com.google.gson.Gson
import android.provider.Settings
import com.fastfollow.bytefollow.model.MeResponse
import com.fastfollow.bytefollow.model.UserDetail
import java.util.*

class UserStorage(var context: Context) {

    private var sharedPreferences: SharedPreferences =
        context.getSharedPreferences("com.fastfollow.bytefollow",Context.MODE_PRIVATE)

    var username : String
        get() = sharedPreferences.getString("username","")?:""
        set(value) { sharedPreferences.edit().putString("username",value).apply() }

    var userId : String
        get() = sharedPreferences.getString("user_id","")?:""
        set(value) { sharedPreferences.edit().putString("user_id",value).apply() }

    var cookie : String
        get() = sharedPreferences.getString("cookie","")?:""
        set(value) { sharedPreferences.edit().putString("cookie",value).apply() }

    var userDetail : UserDetail
        get() { val info = sharedPreferences.getString("user_info","{}"); return Gson().fromJson(info,UserDetail::class.java) }
        set(value) { sharedPreferences.edit().putString("user_info",Gson().toJson(value)).apply() }

    var deviceId : String
        @SuppressLint("HardwareIds")
        get() { return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) }
        set(value) {}

    val country: String
        get() = Locale.getDefault().country;

    val language: String
        get() = Locale.getDefault().language;

    var meInfo : MeResponse
        get() { val info = sharedPreferences.getString("me_info","{}"); return Gson().fromJson(info,MeResponse::class.java) }
        set(value) { sharedPreferences.edit().putString("me_info",Gson().toJson(value)).apply() }

    var credit : Int
        get() { return sharedPreferences.getInt("client_credit",0) }
        set(value) { sharedPreferences.edit().putInt("client_credit",value).apply() }

    fun clearDB()
    {
        sharedPreferences.edit().remove("username").apply()
        sharedPreferences.edit().remove("user_id").apply()
        sharedPreferences.edit().remove("cookie").apply()
        sharedPreferences.edit().remove("user_info").apply()
    }



}