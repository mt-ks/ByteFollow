package com.fastfollow.bytefollow.helpers

class URIControl (val uri : String) {

    public fun checkType() : Int
    {
        return if (uri.contains("video")) 2 else 1
    }

}