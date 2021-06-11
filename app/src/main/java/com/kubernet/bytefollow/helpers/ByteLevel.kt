package com.kubernet.bytefollow.helpers

import android.util.Base64
import com.kubernet.bytefollow.model.ByteLevelModel
import com.google.gson.Gson
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class ByteLevel {
    val hash : String = "695a4f57654e6c793557684249526338622f634f46445930455046307532546a747643757062734170326b3d"


    fun decrypt(hash: String, ivValue: String, encryptedData: String, macValue: String) : String
    {
        val keyValue : ByteArray = Base64.decode(String(hash.decodeHex()), Base64.DEFAULT)
        val key : SecretKeySpec = SecretKeySpec(keyValue,"AES")
        val iv : ByteArray = Base64.decode(ivValue.toByteArray(Charsets.UTF_8), Base64.DEFAULT)
        val decodedValue = Base64.decode(encryptedData.toByteArray(), Base64.DEFAULT)

        val macKey = SecretKeySpec(keyValue,"HmacSHA256")
        val hmacSha256 = Mac.getInstance("HmacSHA256")
        hmacSha256.init(macKey)
        hmacSha256.update(ivValue.toByteArray(Charsets.UTF_8))

        val cipher : Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE,key, IvParameterSpec(iv))
        val decValue : ByteArray = cipher.doFinal(decodedValue)

        var firstQuoteIndex : Int = 0
        while (decValue[firstQuoteIndex] != '"'.toByte()) firstQuoteIndex++
        return String(decValue.copyOfRange(firstQuoteIndex + 1, decValue.size - 2))
    }

    fun encrypt(hash : String, plainText : String) : String{
        val keyValue : ByteArray = Base64.decode(String(hash.decodeHex()), Base64.DEFAULT)
        val key = SecretKeySpec(keyValue,"AES")
        val serializedPlaintext : String = "s:" + plainText.toByteArray().size + ":\"" + plainText + "\";"
        val plaintextBytes : ByteArray = serializedPlaintext.toByteArray(Charsets.UTF_8)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE,key)

        val iv : ByteArray = cipher.iv
        val encVal : ByteArray = cipher.doFinal(plaintextBytes)
        val encryptedData : String = Base64.encodeToString(encVal, Base64.NO_WRAP)

        val macKey = SecretKeySpec(keyValue,"HmacSHA256")
        val hmacSha256 : Mac = Mac.getInstance("HmacSHA256")
        hmacSha256.init(macKey)
        hmacSha256.update(Base64.encode(iv, Base64.NO_WRAP))
        val calcMac : ByteArray = hmacSha256.doFinal(encryptedData.toByteArray(Charsets.UTF_8))
        val mac = calcMac.toHexString()


        val model = ByteLevelModel(Base64.encodeToString(iv, Base64.NO_WRAP),encryptedData,mac)

        val aesDataJson = Gson().toJson(model)

        return Base64.encodeToString(aesDataJson.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
    }

    private fun String.decodeHex(): ByteArray {
        require(length % 2 == 0) { "Must have an even length" }
        return chunked(2)
            .map { it.toInt(16).toByte() }
            .toByteArray()
    }

    private fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }



}