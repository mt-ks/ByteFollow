package com.kubernet.bytefollow.helpers

import android.util.Log
import com.kubernet.bytefollow.model.UserDetail
import com.kubernet.bytefollow.model.VideoDetail
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import org.json.JSONObject
import java.util.regex.Matcher
import java.util.regex.Pattern

class UserRequireChecker(private val responseBody: ResponseBody) {
    lateinit var userDetail : UserDetail
    lateinit var videoDetail : VideoDetail
    lateinit var itemList : List<VideoDetail>
    var responseString : String = responseBody.string()

    fun checkUser() : Boolean
    {
        val pattern : Pattern = Pattern.compile(
            "(.*?)<script id=\"__NEXT_DATA__\" type=\"application/json\"(.*?)>(.*?)</script>(.*?)",
            Pattern.CASE_INSENSITIVE or Pattern.DOTALL)
        val matcher : Matcher = pattern.matcher(responseString)
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

    fun checkVideo() : Boolean
    {
        val pattern : Pattern = Pattern.compile(
            "(.*?)<script id=\"__NEXT_DATA__\" type=\"application/json\"(.*?)>(.*?)</script>(.*?)",
            Pattern.CASE_INSENSITIVE or Pattern.DOTALL)

        val matcher : Matcher = pattern.matcher(responseString)
        if(matcher.matches() && matcher.groupCount() >= 3)
        {
            val dataMatches : String = matcher.group(3)?:""
            val jsonData : JSONObject = JSONObject(dataMatches)
            if(JsonFieldChecker("props>pageProps>itemInfo>itemStruct",jsonData).check()){
                val jsonDataInfo = jsonData.getJSONObject("props").getJSONObject("pageProps").getJSONObject("itemInfo").getJSONObject("itemStruct");
                videoDetail = Gson().fromJson(jsonDataInfo.toString(),VideoDetail::class.java)
                Log.d("VIDEOCHECK",jsonDataInfo.toString())
                return true;
            }
            return false;
        }
        return false;
    }

    fun checkItems() : Boolean
    {
        val pattern : Pattern = Pattern.compile(
            "(.*?)<script id=\"__NEXT_DATA__\" type=\"application/json\"(.*?)>(.*?)</script>(.*?)",
            Pattern.CASE_INSENSITIVE or Pattern.DOTALL)

        val matcher : Matcher = pattern.matcher(responseString)
        if(matcher.matches() && matcher.groupCount() >= 3)
        {
            val dataMatches : String = matcher.group(3)?:""
            val jsonData : JSONObject = JSONObject(dataMatches)
            if(JsonFieldChecker("props>pageProps>items",jsonData).check()){
                val jsonDataInfo = jsonData.getJSONObject("props").getJSONObject("pageProps").getJSONArray("items");
                val typeToken = object : TypeToken<List<VideoDetail>>(){}.type
                itemList = Gson().fromJson(jsonDataInfo.toString(),typeToken)
                Log.d("ITEMS CHECK",jsonDataInfo.toString())
                return true;
            }
            return false;
        }
        return false;
    }

}