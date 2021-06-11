package com.kubernet.bytefollow.model

data class OembedResponse(
    var version : String,
    var type : String,
    var author_url : String,
    var html : String,
    var thumbnail_url : String
)