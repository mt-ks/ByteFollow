package com.kubernet.bytefollow.service


import android.text.TextUtils
import android.util.Base64
import com.kubernet.bytefollow.helpers.ByteLevel
import com.kubernet.bytefollow.model.ByteLevelModel
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Response
import okhttp3.ResponseBody
import java.nio.charset.StandardCharsets

class InterceptorDec : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val newResponse = response.newBuilder()

        return try{
            var contentType = response.header("Content-Type")
            if (TextUtils.isEmpty("Content-Type")) contentType = "application/json"
            val responseString : String  = response.body()?.string()?:""
            val base64_decode = String(Base64.decode(responseString, Base64.DEFAULT), StandardCharsets.UTF_8)
            val byteModel : ByteLevelModel = Gson().fromJson(base64_decode, ByteLevelModel::class.java)
            var decryptedString = ""
            val byteLevel = ByteLevel()
            decryptedString = byteLevel.decrypt(byteLevel.hash, byteModel.iv, byteModel.value, byteModel.mac)
            newResponse.body(ResponseBody.create(MediaType.parse(contentType!!), decryptedString))
            newResponse.build()
        }catch (e : Exception) {
            response
        }

    }
}