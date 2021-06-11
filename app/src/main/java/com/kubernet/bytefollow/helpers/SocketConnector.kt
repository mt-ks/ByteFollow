package com.kubernet.bytefollow.helpers

import android.annotation.SuppressLint
import android.content.Context
import com.kubernet.bytefollow.storage.UserStorage
import io.socket.client.IO
import io.socket.client.Socket
import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class SocketConnector(val context : Context, private val connectionType: String){
    private var socket : Socket? = null
    private val userStorage : UserStorage = UserStorage(context)

    class UnsafeOkHttpClient {
        companion object {
            fun getUnsafeOkHttpClient(): OkHttpClient.Builder? {
                try {
                    val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                        @SuppressLint("TrustAllX509TrustManager")
                        @Throws(CertificateException::class)
                        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                        }

                        @SuppressLint("TrustAllX509TrustManager")
                        @Throws(CertificateException::class)
                        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                        }

                        override fun getAcceptedIssuers(): Array<X509Certificate> {
                            return arrayOf()
                        }
                    })

                    val sslContext = SSLContext.getInstance("SSL")
                    sslContext.init(null, trustAllCerts, SecureRandom())
                    val sslSocketFactory = sslContext.socketFactory

                    val builder = OkHttpClient.Builder()
                    builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                     builder.hostnameVerifier { _, _ -> true }

                    return builder
                } catch (e: Exception) {
                    return null
                }
            }
        }
    }


    init {
        try{

            val deviceId = arrayOf(userStorage.deviceId)
            val igId = arrayOf(userStorage.userId)
            val xToken = arrayOf("025d5a28ae66e566ce18e3c72f7d0da6")
            val connType = arrayOf(connectionType)

            val map : HashMap<String, List<String>> = HashMap()
            map.put("device_id", deviceId.toList())
            map.put("ig_id", igId.toList())
            map.put("x-token",xToken.toList())
            map.put("conn_type",connType.toList())
            val opt = IO.Options()

            UnsafeOkHttpClient.getUnsafeOkHttpClient()?.build().let {
                opt.webSocketFactory = it
                opt.callFactory = it
            }
            opt.extraHeaders = map

            socket = IO.socket("wss://bytefollower.com:6007",opt)



            socket?.connect()
            socket?.on(Socket.EVENT_CONNECT_ERROR) {
                println(it.size)
                println(it[0].toString())
            }
            socket?.on(Socket.EVENT_CONNECT) {
                println(it)
            }
        }catch (e : java.lang.Exception){
            e.printStackTrace()
        }
    }

    fun disconnect()
    {
        try {
            socket?.disconnect()
        }catch (e : Exception){}
    }

}