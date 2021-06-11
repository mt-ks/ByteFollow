package com.kubernet.bytefollow.helpers

interface NextUInterface {
    fun onDownloadFinished(downloadPath: String)
    fun onProgress(progress : Int)
    fun onDownloadError(message : String)
}