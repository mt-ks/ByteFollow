package com.kubernet.bytefollow.service

import com.kubernet.bytefollow.helpers.ByteLevel
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer

class InterceptorEnc: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        var request = chain.request()
        val oldBody = request.body()

        try{
            if(request.method().equals("POST")){
                val buffer = Buffer()
                oldBody?.writeTo(buffer)
                val strOldBody = buffer.readUtf8()
                val mediaType : MediaType? = MediaType.parse("text/plain; charset=utf-8")
                var strNewBody : String = ""
                val byteLevel = ByteLevel()
                strNewBody = byteLevel.encrypt(byteLevel.hash,strOldBody)

                val body = RequestBody.create(mediaType,strNewBody)
                request = request.newBuilder()
                    .header("Content-Type",body.contentType().toString())
                    .header("Content-Length",body.contentLength().toString())
                    .method(request.method(),body)
                    .build()
            }

        }catch (e : Exception) { }

        return chain.proceed(request)
    }
}