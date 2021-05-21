package com.fastfollow.bytefollow.helpers

import android.util.Log
import com.fastfollow.bytefollow.model.UserDetail
import com.fastfollow.bytefollow.model.UserInfo
import com.google.gson.Gson
import okhttp3.ResponseBody
import org.json.JSONObject
import java.util.regex.Matcher
import java.util.regex.Pattern

class UserRequireChecker(private val responseBody: ResponseBody) {
    lateinit var userDetail : UserDetail

    fun checkUser() : Boolean
    {
        val pattern : Pattern = Pattern.compile(
            "(.*?)<script id=\"__NEXT_DATA__\" type=\"application/json\"(.*?)>(.*?)</script>(.*?)",
            Pattern.CASE_INSENSITIVE or Pattern.DOTALL)
        val matcher : Matcher = pattern.matcher(responseBody.string())
        if(matcher.matches() && matcher.groupCount() >= 3)
        {
            val dataMatches : String = matcher.group(3)?:""
            val jsonData : JSONObject = JSONObject(dataMatches)
            if(JsonFieldChecker("props>pageProps>userInfo>user",jsonData).check()){
                val jsonDataInfo = jsonData.getJSONObject("props").getJSONObject("pageProps").getJSONObject("userInfo");
                userDetail = Gson().fromJson(jsonDataInfo.toString(),UserDetail::class.java)
                Log.d("USERCHECK",jsonDataInfo.toString())
                return true;
            }
            return false;
        }
        return false;
    }

}