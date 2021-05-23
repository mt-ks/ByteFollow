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
import com.fastfollow.bytefollow.helpers.UserRequireChecker
import com.fastfollow.bytefollow.model.OrderDetailModel
import com.fastfollow.bytefollow.model.OrderModel
import com.fastfollow.bytefollow.service.BFApi
import com.fastfollow.bytefollow.service.BFClient
import com.fastfollow.bytefollow.service.TKApi
import com.fastfollow.bytefollow.service.TKClient
import com.fastfollow.bytefollow.storage.UserStorage
import com.fastfollow.bytefollow.ui.profile.ProfileViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.math.roundToInt

class ReactionFragment : Fragment() {

    private val TAG = "ReactionFragment"
    private val viewModel : ReactionViewModel by activityViewModels()
    private val profileViewModel : ProfileViewModel by activityViewModels()
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
        binding.webView.loadUrl("about:blank")
        receiveOrders()

        viewModel.waitOrderTime.observe(viewLifecycleOwner,{
            binding.waitTimer.text = "$it seconds..."
            if (it == 0)
            {
                receiveOrders()
            }
        })

    }


    private fun receiveOrders()
    {

        if (userStorage.received_orders.size != 0){
            Log.d(TAG,"receiveOrder isNot Empty")
            handleOrder()
            return;
        }
        binding.webView.loadUrl("about:blank")
        val api = (BFClient(requireContext())).getClient().create(BFApi::class.java)
        compositeDisposable?.add(api.newOrder().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Log.d(TAG,"Order received with request")
                if (it.orders.size > 0)
                {
                    userStorage.received_orders = it.orders
                    handleOrder();
                }else{
                    countDownInit(10)
                }
            },{
                it.printStackTrace()
            }))
    }

    private fun handleOrder()
    {
        val link = userStorage.received_orders[0].order.link
        val type = URIControl(link).checkType();
        myWebViewClient.injectMethod = type
        myWebViewClient.isJSExecuted = false
        binding.webView.loadUrl(link)
    }

    private fun checkOrder()
    {

        Log.d(TAG,"checkOrder()")
        val link = userStorage.received_orders[0].order.link
        val uriControl = URIControl(link)
        val type = uriControl.checkType()
        if (type == 1)
        {
            checkIsFollowed(uriControl.parseVideoUsername())
        }else{
            checkIsLiked(uriControl.parseVideoUsername(),uriControl.parseVideoCode())
        }
    }

    private fun checkIsFollowed(username : String){
        Log.d(TAG, "check will:$username")
        val api = (TKClient(requireContext())).getClient().create(TKApi::class.java)
        compositeDisposable?.add(api.getUserInfo(username).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val user = UserRequireChecker(it);
                if (user.checkUser() && user.userDetail.user.relation == 1)
                {
                    updateOrder(1,userStorage.received_orders[0].id)
                }else{
                    updateOrder(2,userStorage.received_orders[0].id)
                }
                Log.d(TAG,"follow relation: ${user.userDetail.user.relation}")
                val tmpOrder = userStorage.received_orders
                tmpOrder.removeAt(0)
                userStorage.received_orders = tmpOrder
                Log.d(TAG,"Orders remains: " + userStorage.received_orders.size)
                myWebViewClient.isJSExecuted = false
                binding.webView.loadUrl("about:blank")
            },{
                it.printStackTrace()
            }))
    }

    private fun checkIsLiked(username: String, videoID: String)
    {
        val api = (TKClient(requireContext())).getClient().create(TKApi::class.java)
        compositeDisposable?.add(api.getVideoInfo(username,videoID).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val video = UserRequireChecker(it);
                if (video.checkVideo() && video.videoDetail.digged)
                {

                }
            },{
                it.printStackTrace()

            }))
    }



    private fun countDownInit(seconds : Int)
    {
        timer = object : CountDownTimer((seconds * 1000).toLong(),1000){
            override fun onTick(p0: Long) {
                val remainSecond = (p0 / 1000).toDouble().roundToInt()
                Log.d(TAG,"remains" + remainSecond)
                if (remainSecond != 0) viewModel.waitOrderTime.value = remainSecond
            }

            override fun onFinish() {
                Log.d(TAG,"onFinish timer")
                viewModel.waitOrderTime.value = 0
                timer?.cancel()
            }

        }.start()
    }

    private fun updateOrder(status : Int,order_id:Int)
    {
        Log.d(TAG,"updateOrder()")
        val api = (BFClient(requireActivity())).getClient().create(BFApi::class.java)
        compositeDisposable?.add(api.check(status,order_id).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                profileViewModel.currentCredit.value = it.client
                receiveOrders()
            },{
                it.printStackTrace()
            }))
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
                            "\t(document.getElementsByClassName(\"guide\")).length > 0 ? (document.getElementsByClassName(\"guide\"))[0].outerHTML = \"\" : \"\";\n" +
                            "\t(document.getElementsByClassName(\"mask\")).length > 0 ? (document.getElementsByClassName(\"mask\"))[0].outerHTML = \"\" : \"\";\n" +
                            "\n" +
                            "\tsetTimeout(function() {\n" +
                            "\t\t(document.getElementsByClassName(\"follow-button\"))[0].click();\n" +
                            "\t}, 1000);\n" +
                            "\n" +
                            "})();")
                }else{
                    view!!.loadUrl("javascript:(function() {\n" +
                            "\t(document.getElementsByClassName(\"guide\")).length > 0 ? (document.getElementsByClassName(\"guide\"))[0].outerHTML = \"\" : \"\";\n" +
                            "\t(document.getElementsByClassName(\"mask\")).length > 0 ? (document.getElementsByClassName(\"mask\"))[0].outerHTML = \"\" : \"\";\n" +
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
                    reactionFragment.checkOrder()
                }
            }
            return super.shouldInterceptRequest(view, request)
        }
    }
}