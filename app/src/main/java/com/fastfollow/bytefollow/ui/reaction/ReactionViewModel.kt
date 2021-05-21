package com.fastfollow.bytefollow.ui.reaction

import android.webkit.WebView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ReactionViewModel : ViewModel() {

    val isWebViewLoaded : MutableLiveData<Boolean> by lazy{
        MutableLiveData<Boolean>()
    }

}