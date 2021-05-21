package com.fastfollow.bytefollow.ui.reaction

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.fastfollow.bytefollow.databinding.FragmentReactionBinding
import io.reactivex.disposables.CompositeDisposable
import java.util.*

class ReactionFragment : Fragment() {

    private val viewModel : ReactionViewModel by activityViewModels()


    private var _binding : FragmentReactionBinding? = null
    private val binding get() = _binding!!
    private lateinit var myWebViewClient : MyWebViewClient
    private var compositeDisposable : CompositeDisposable? = null
    private var webViewBundle: Bundle?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReactionBinding.inflate(inflater,container,false)
        compositeDisposable = CompositeDisposable()
        myWebViewClient = MyWebViewClient();

        if (webViewBundle == null)
        {
            binding.webView.settings.domStorageEnabled = true
            binding.webView.settings.javaScriptEnabled = true
            binding.webView.webViewClient  = myWebViewClient
            binding.webView.loadUrl("https://www.tiktok.com/@cznburak")
        }else{
            binding.webView.restoreState(webViewBundle!!)
        }

        return _binding!!.root
    }


    override fun onPause() {
        super.onPause()
        webViewBundle = Bundle()
        binding.webView.saveState(webViewBundle!!)
        println("OnPause Worked")
    }

    private class MyWebViewClient : WebViewClient() {
        var isJSExecuted : Boolean = false
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            if (!isJSExecuted)
            {
                isJSExecuted = true;
                view!!.loadUrl("javascript:(function() {\n" +
                        "\t(document.getElementsByClassName(\"tab\"))[0].hidden = true;\n" +
                        "\t(document.getElementsByClassName(\"video-list\"))[0].innerHTML = \"\";\n" +
                        "\t(document.getElementsByClassName(\"footer-bar-container\")).length > 0 ? (document.getElementsByClassName(\"footer-bar-container\"))[0].outerHTML = \"\" : \"\";\n" +
                        "\n" +
                        "\tsetTimeout(function() {\n" +
                        "\t\t(document.getElementsByClassName(\"follow-button\"))[0].click();\n" +
                        "\t}, 1000);\n" +
                        "\n" +
                        "})();")
            }
        }

        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
        ): WebResourceResponse? {
            if (request?.method?.toLowerCase(Locale.ROOT) == "post")
            {

                val uri : String = request.url.toString()
                if (uri.contains("follow/user")){
                    println("User followed please check!")
                }
            }
            return super.shouldInterceptRequest(view, request)
        }
    }
}