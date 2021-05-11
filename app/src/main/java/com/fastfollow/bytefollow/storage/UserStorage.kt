package com.fastfollow.bytefollow.storage

import android.content.Context
import android.content.SharedPreferences

class UserStorage(var context: Context) {

    private var sharedPreferences: SharedPreferences =
        context.getSharedPreferences("com.fastfollow.bytefollow",Context.MODE_PRIVATE)

    var username
        get() = sharedPreferences.getString("username","")
        set(value) { sharedPreferences.edit().putString("username",value).apply() }

    var user_id
        get() = sharedPreferences.getString("user_id","")
        set(value) { sharedPreferences.edit().putString("user_id",value).apply() }

    var avatar_url
        get() = sharedPreferences.getString("avatar_url","")
        set(value) { sharedPreferences.edit().putString("user_id",value).apply() }

    var cookie
        get() = sharedPreferences.getString("cookie","")
        set(value) { sharedPreferences.edit().putString("cookie",value).apply() }

    fun clearDB()
    {
        sharedPreferences.edit().remove("username").apply()
        sharedPreferences.edit().remove("user_id").apply()
        sharedPreferences.edit().remove("avatar_url").apply()
        sharedPreferences.edit().remove("cookie").apply()
    }



}