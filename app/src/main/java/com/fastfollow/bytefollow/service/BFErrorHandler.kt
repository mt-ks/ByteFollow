package com.fastfollow.bytefollow.service

import android.app.Activity
import android.util.Log
import com.fastfollow.bytefollow.AppActivity
import com.fastfollow.bytefollow.R
import com.fastfollow.bytefollow.dialogs.LogoutDialog
import com.fastfollow.bytefollow.dialogs.UpdateDialog
import org.json.JSONObject
import retrofit2.HttpException

class BFErrorHandler(private val activity: Activity, err : Throwable) {
    var TAG = "BFErrorHandler"
    var message = "An error occured"
    init{
        if (err is HttpException)
        {
            val body : String = err.response()?.errorBody()?.string().toString()
            checkWithResponse(body)
        }
    }

    private fun checkWithResponse(response : String)
    {
        try{
            val jsonObject = JSONObject(response)
            if(jsonObject.has("message"))
            {
                message = jsonObject.getString("message")
            }
            checkHasUpdate(jsonObject)
            checkIsLogout(jsonObject)
        }catch (e : Exception) { }
    }

    private fun checkHasUpdate(jsonObject: JSONObject)
    {
        if (jsonObject.has("need_upgrade"))
        {
            val route = jsonObject.getString("route")
            var downloadName = "ByteFollow.apk"
            if (jsonObject.has("download_name"))
            {
               downloadName = jsonObject.getString("download_name")
            }
            (UpdateDialog(activity,route,downloadName)).show((activity as AppActivity).supportFragmentManager,"UpdateDialog")
        }
    }

    private fun checkIsLogout(jsonObject: JSONObject)
    {
        if (jsonObject.has("message") && jsonObject.getString("message") == "bad_auth")
        {
            (LogoutDialog(activity)).start()
        }
    }

}