package com.fastfollow.bytefollow.helpers

import java.util.regex.Matcher
import java.util.regex.Pattern

class URIControl (private var uri : String) {

    fun checkType() : Int
    {
        return if (uri.contains("video")) 2 else 1
    }

    fun parseVideoCode() : String
    {
        var videoCode = ""
        val uriSplit = uri.split("?")
        var pureLink = uriSplit[0]
        val lastCharacter = uri[uri.length - 1];
        pureLink = if(lastCharacter == '/') pureLink else "$pureLink/"

        val pattern : Pattern = Pattern.compile("(.*?)/@(.*?)/video/(.*?)/")
        val matcher : Matcher = pattern.matcher(pureLink)

        if(matcher.matches() && matcher.groupCount() >= 2)
        {
            videoCode = matcher.group(3)?:""
        }
        return videoCode
    }

    fun parseVideoUsername() : String {
        var username = ""
        val uriSplit = uri.split("?")
        var pureLink = uriSplit[0]
        val lastCharacter = pureLink[pureLink.length - 1];
        pureLink = if(lastCharacter == '/') pureLink else "$pureLink/"

        val pattern : Pattern = Pattern.compile("(.*?)/@(.*?)/(.*?)")
        val matcher : Matcher = pattern.matcher(pureLink)

        if(matcher.matches() && matcher.groupCount() >= 2)
        {
            username = matcher.group(2)?:""
        }
        return username
    }

    fun parseVideoURL() : String?
    {
        var response : String? = null
        val pattern : Pattern = Pattern.compile("(.*?)/v/(.*?).html(.*?)")
        val matcher : Matcher = pattern.matcher(uri)

        if(matcher.matches()){
            response = matcher.group(2)
        }
        return response
    }

    fun parseShortenCode() : String?
    {
        var shortCode: String? = null
        val uriSplit = uri.split("?")
        var pureLink = uriSplit[0]
        val lastCharacter = pureLink[pureLink.length - 1];
        pureLink = if(lastCharacter == '/') pureLink else "$pureLink/"

        val pattern : Pattern = Pattern.compile("(.*?).com/(.*?)/")
        val matcher : Matcher = pattern.matcher(pureLink)

        if(matcher.matches() && matcher.groupCount() >= 2)
        {
            shortCode = matcher.group(2)?:""
        }
        return shortCode
    }



}