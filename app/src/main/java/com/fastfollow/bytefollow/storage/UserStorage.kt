package com.fastfollow.bytefollow.storage

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.fastfollow.bytefollow.model.UserInfo
import com.google.gson.Gson
import android.provider.Settings
import com.fastfollow.bytefollow.model.UserDetail
import java.util.*

class UserStorage(var context: Context) {

    private var sharedPreferences: SharedPreferences =
        context.getSharedPreferences("com.fastfollow.bytefollow",Context.MODE_PRIVATE)

    var username : String
        get() = sharedPreferences.getString("username","")?:""
        set(value) { sharedPreferences.edit().putString("username",value).apply() }

    var user_id : String
        get() = sharedPreferences.getString("user_id","")?:""
        set(value) { sharedPreferences.edit().putString("user_id",value).apply() }

    var cookie : String
        get() = sharedPreferences.getString("cookie","")?:""
        set(value) { sharedPreferences.edit().putString("cookie",value).apply() }

    var user_detail : UserDetail
        get() { val info = sharedPreferences.getString("user_info","{}"); return Gson().fromJson(info,UserDetail::class.java) }
        set(value) { sharedPreferences.edit().putString("user_info",Gson().toJson(value)).apply() }

    var device_id : String
        @SuppressLint("HardwareIds")
        get() { return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) }
        set(value) {}

    val country
        get() = Locale.getDefault().country;

    val language
        get() = Locale.getDefault().language;


    fun clearDB()
    {
        sharedPreferences.edit().remove("username").apply()
        sharedPreferences.edit().remove("user_id").apply()
        sharedPreferences.edit().remove("cookie").apply()
        sharedPreferences.edit().remove("user_info").apply()
    }



}