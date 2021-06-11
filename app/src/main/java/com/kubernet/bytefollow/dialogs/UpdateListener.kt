package com.kubernet.bytefollow.dialogs

interface UpdateListener {
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
}