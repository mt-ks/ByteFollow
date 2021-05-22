package com.fastfollow.bytefollow.ui.reaction

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.fastfollow.bytefollow.databinding.FragmentReactionBinding
import com.fastfollow.bytefollow.helpers.URIControl
import com.fastfollow.bytefollow.service.BFApi
import com.fastfollow.bytefollow.service.BFClient
import com.fastfollow.bytefollow.storage.UserStorage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.math.roundToInt

class ReactionFragment : Fragment() {

    private val TAG = "ReactionFragment"
    private val viewModel : ReactionViewModel by activityViewModels()
    private var _binding : FragmentReactionBinding? = null
    private val binding get() = _binding!!
    private lateinit var myWebViewClient : MyWebViewClient
    private var compositeDisposable : CompositeDisposable? = null
    private lateinit var userStorage: UserStorage
    private var timer : CountDownTimer? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentReactionBinding.inflate(inflater,container,false)
        compositeDisposable = CompositeDisposable()
        myWebViewClient = MyWebViewClient(this);
        userStorage = UserStorage(requireContext())
        return _binding!!.root
    }

    @SuppressLint("SetJavaScriptEnabled", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.webView.settings.domStorageEnabled = true
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.webViewClient  = myWebViewClient
        //binding.webView.loadUrl("https://www.tiktok.com/@cznburak")
        receiveOrders()

        viewModel.orderModel.observe(viewLifecycleOwner, {
            userStorage.received_orders = it
        })

        viewModel.waitOrderTime.observe(viewLifecycleOwner,{
            binding.waitTimer.text = "$it seconds..."
            if (it == 0)
            {
                Log.d(TAG,"Finished")
            }
        })

    }

    private fun receiveOrders()
    {
        if (userStorage.received_orders.isNotEmpty()){
            handleOrder()
            viewModel.orderModel.value = userStorage.received_orders
            return;
        }
        val api = (BFClient(requireContext())).getClient().create(BFApi::class.java)
        compositeDisposable?.add(api.newOrder().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d(TAG,"Request success")
                viewModel.orderModel.value = it.orders
                handleOrder();
            },{
                it.printStackTrace()
            }))
    }

    private fun handleOrder()
    {
        viewModel.orderModel.value?.get(0)?.order?.link?.let {
            val type = URIControl(it).checkType();
            if (type == 1)
            {
                myWebViewClient.injectMethod = 1
                binding.webView.loadUrl(it)
            }
        }
    }

    private fun checkOrder(type : Int)
    {

    }

    private fun checkIsFollowed(){

    }

    private fun checkIsLiked()
    {

    }

    private fun countDownInit(seconds : Int)
    {

        timer = object : CountDownTimer((seconds * 1000).toLong(),1000){
            override fun onTick(p0: Long) {
                val remainSecond = (p0 / 1000).toDouble().roundToInt()
                if (remainSecond != 0) viewModel.waitOrderTime.value = remainSecond
            }

            override fun onFinish() {
                viewModel.waitOrderTime.value = 0
            }

        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
        compositeDisposable?.clear()
    }

    private class MyWebViewClient(val reactionFragment: ReactionFragment) : WebViewClient() {
        var isJSExecuted : Boolean = false
        var injectMethod : Int = 1;
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            if (!isJSExecuted)
            {
                isJSExecuted = true;
                if (injectMethod == 1){
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
                }else{
                    view!!.loadUrl("javascript:(function() {\n" +
                            "\tsetTimeout(function() {\n" +
                            "\t\t(document.getElementsByClassName(\"heart-twink\"))[0].click()\n" +
                            "\t}, 1000);\n" +
                            "\n" +
                            "})();")
                }
            }
        }

        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
        ): WebResourceResponse? {
            if (request?.method?.toLowerCase(Locale.ROOT) == "post")
            {
                val uri : String = request.url.toString()
                if (uri.contains("follow/user") || uri.contains("item/digg")){
                    Log.d(reactionFragment.TAG,"User followed please check!")
                    val type = if(uri.contains("follow/user")) 1 else 2
                    reactionFragment.checkOrder(type)
                }
            }
            return super.shouldInterceptRequest(view, request)
        }
    }
}