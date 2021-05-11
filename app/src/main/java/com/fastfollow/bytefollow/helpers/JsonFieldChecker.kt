package com.fastfollow.bytefollow.helpers

import org.json.JSONObject

class JsonFieldChecker(var field: String, var jsonData: JSONObject) {
    fun check() : Boolean {
        val data : List<String>  = field.split(">")
        for (i in 0..data.size) {
            if (!jsonData.has(data[i])){
                return false;
            }
            if (i != data.size - 1){
                jsonData = jsonData.getJSONObject(data[i]);
            }
        }
        return true
    }
}