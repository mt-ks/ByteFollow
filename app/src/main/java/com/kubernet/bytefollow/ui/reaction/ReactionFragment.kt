package com.kubernet.bytefollow.ui.reaction

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.kubernet.bytefollow.R
import com.kubernet.bytefollow.databinding.FragmentReactionBinding
import com.kubernet.bytefollow.dialogs.LogoutDialog
import com.kubernet.bytefollow.helpers.*
import com.kubernet.bytefollow.service.BFApi
import com.kubernet.bytefollow.service.BFClient
import com.kubernet.bytefollow.service.TKApi
import com.kubernet.bytefollow.service.TKClient
import com.kubernet.bytefollow.storage.UserStorage
import com.kubernet.bytefollow.ui.profile.ProfileViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception
import java.util.*
import kotlin.math.roundToInt


class ReactionFragment : SessionInterface ,Fragment() {

    private val TAG = "ReactionFragment"
    private val countDownSeconds = 60
    private val viewModel : ReactionViewModel by activityViewModels()
    private val profileViewModel : ProfileViewModel by activityViewModels()
    private var _binding : FragmentReactionBinding? = null
    private val binding get() = _binding!!
    private lateinit var myWebViewClient : MyWebViewClient
    private var compositeDisposable : CompositeDisposable? = null
    private lateinit var userStorage: UserStorage
    private var timer : CountDownTimer? = null
    private var delayTimer : CountDownTimer? = null;
    private var actionTimeout : CountDownTimer? = null
    private var socketConnector : SocketConnector? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentReactionBinding.inflate(inflater,container,false)
        compositeDisposable = CompositeDisposable()
        myWebViewClient = MyWebViewClient(this);
        userStorage = UserStorage(requireContext())
        socketConnector = SocketConnector(requireContext(),"BACKGROUND")
        return _binding!!.root
    }

    @SuppressLint("SetJavaScriptEnabled", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.webView.settings.domStorageEnabled = true
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.webViewClient  = myWebViewClient
        binding.webView.loadUrl("about:blank")

        if (viewModel.waitOrderTime.value == null) receiveOrders() else countDownInit(viewModel.waitOrderTime.value!!)


        viewModel.waitOrderTime.observe(viewLifecycleOwner,{
            binding.lastStatus.text = String.format(getString(R.string.no_new_order_wait_x_seconds),it.toString())
            if (it == 0)
            {
                receiveOrders()
            }
        })

        viewModel.lastStatus.observe(viewLifecycleOwner,{
            try {
                binding.lastStatus.text = it
            }catch (e : Exception){}
        })

        viewModel.reactionStatus.observe(viewLifecycleOwner,{
            binding.reactionStatus.text = it
        })

        viewModel.reactionDisplayUrl.observe(viewLifecycleOwner,{
            Glide.with(requireContext()).load(it).into(binding.userAvatar)
        })
        viewModel.reactionUsername.observe(viewLifecycleOwner,{
            binding.usernameField.text = it
        })

    }


    private fun receiveOrders()
    {
        actionTimeout?.cancel()
        timeoutDelayCheck()
        val errorsCount = viewModel.actionErrorCount.value?:0
        Log.d(TAG,"errors count $errorsCount")
        if (errorsCount > 3)
        {
            SessionChecker(requireActivity(),this)
        }

        if(errorsCount > 10)
        {
            val logoutDialog = LogoutDialog(requireActivity())
            logoutDialog.customTitle   = getString(R.string.tiktok_limit)
            logoutDialog.customMessage = getString(R.string.tiktok_limit_desc)
            logoutDialog.start()
            stopAllActions()
            return
        }

        viewModel.lastStatus.value = getString(R.string.all_orders_checking)
        if (userStorage.received_orders.size != 0){
            Log.d(TAG,"receiveOrder isNot Empty")
            checkPreviousOrderState()
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
                    checkPreviousOrderState()
                }else{
                    countDownInit(countDownSeconds)
                }
            },{
                viewModel.lastStatus.value = getString(R.string.receive_order_error_msg)
                countDownInit(countDownSeconds)
            }))
    }

    private fun handleOrder()
    {

        val link = userStorage.received_orders[0].order.link
        val type = URIControl(link).checkType();
        myWebViewClient.injectMethod = type
        myWebViewClient.isJSExecuted = false
        binding.webView.loadUrl(link)
        if(type == 1)
        {
            viewModel.lastStatus.value = getString(R.string.user_following)
        }else{
            viewModel.lastStatus.value = getString(R.string.video_liking)

        }
    }


    private fun checkOrder()
    {
        Handler(Looper.getMainLooper()).post(Runnable {
            delayTimer = object : CountDownTimer(3000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    viewModel.lastStatus.value = getString(R.string.again_checking)
                }

                override fun onFinish() {
                    delayTimer?.cancel()
                    checkOrderPostDelay()
                }
            }.start()
        })
    }



    fun checkOrderPostDelay()
    {
        Log.d(TAG,"checkOrderPostDelay()")
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

    private fun checkPreviousOrderState()
    {
        viewModel.lastStatus.value = getString(R.string.order_checking)
        binding.stateProgress.visibility = View.VISIBLE
        binding.stateProgress.isIndeterminate = true

        val link = userStorage.received_orders[0].order.link
        val uriControl = URIControl(link)
        val type = uriControl.checkType()
        if(type == 1)
        {
            checkIsFollowedPrevious(uriControl.parseVideoUsername())
        }else{
            checkIsLikedPrevious(uriControl.parseVideoUsername(),uriControl.parseVideoCode())
        }
    }

    private fun checkIsFollowedPrevious(username : String)
    {
        Log.d(TAG, "previous check:$username")
        val api = (TKClient(requireContext())).getClient().create(TKApi::class.java)
        compositeDisposable?.add(api.getUserInfo(username).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val user = UserRequireChecker(it);
                if (user.checkUser())
                {
                    viewModel.reactionDisplayUrl.value = user.userDetail.user.avatarLarger
                    viewModel.reactionUsername.value = user.userDetail.user.uniqueId
                    if(user.userDetail.user.id == userStorage.userDetail.user.id){
                        updateOrder(4,userStorage.received_orders[0].id)
                        removeFirstOrder()
                    }else if (user.userDetail.user.relation == 1)
                    {
                        updateOrder(4,userStorage.received_orders[0].id)
                        removeFirstOrder()
                    }else if(user.userDetail.user.privateAccount){
                        updateOrder(2,userStorage.received_orders[0].id)
                        removeFirstOrder()
                    }else{
                        handleOrder()
                    }
                }else{
                    updateBy404()
                }

            },{
                errorHandler(it)
            }))
    }

    private fun checkIsFollowed(username : String){
        Log.d(TAG, "check will:$username")
        val api = (TKClient(requireContext())).getClient().create(TKApi::class.java)
        compositeDisposable?.add(api.getUserInfo(username).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val user = UserRequireChecker(it);
                if (user.checkUser() && user.userDetail.user.relation == 1)
                {
                    viewModel.reactionStatus.value = String.format(getString(R.string.x_user_followed_success),user.userDetail.user.uniqueId)
                    updateOrder(1,userStorage.received_orders[0].id)
                }else{
                    viewModel.reactionStatus.value = String.format(getString(R.string.x_user_followed_error),user.userDetail.user.uniqueId)
                    updateOrder(2,userStorage.received_orders[0].id)
                }
                removeFirstOrder()
            },{
                errorHandler(it)
            }))
    }


    private fun checkIsLikedPrevious(username: String, videoID: String)
    {
        val api = (TKClient(requireContext())).getClient().create(TKApi::class.java)
        compositeDisposable?.add(api.getVideoInfo(username,videoID).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val video = UserRequireChecker(it);
                if (video.checkVideo())
                {
                    viewModel.reactionDisplayUrl.value = video.videoDetail.video.cover
                    viewModel.reactionUsername.value = video.videoDetail.author.uniqueId
                   if (video.videoDetail.digged)
                   {
                       updateOrder(4,userStorage.received_orders[0].id)
                       removeFirstOrder()
                   }else{
                       handleOrder()
                   }
                }else{
                    updateBy404()
                }
            },{
                errorHandler(it)
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
                    viewModel.reactionStatus.value = String.format(getString(R.string.x_user_like_success),video.videoDetail.author.uniqueId)
                    updateOrder(1,userStorage.received_orders[0].id)
                }else{
                    viewModel.reactionStatus.value = String.format(getString(R.string.x_user_like_error),video.videoDetail.author.uniqueId)
                    updateOrder(2,userStorage.received_orders[0].id)
                }
                removeFirstOrder()
            },{
                errorHandler(it)
            }))
    }


    private fun timeoutDelayCheck()
    {
        actionTimeout = object : CountDownTimer(60*1000,1000){
            override fun onTick(p0: Long) {
                val second = (p0 / 1000).toInt()
                if (second != 0)
                    viewModel.allActionDelaySecond.value = second

                Log.d(TAG,"Delay second is: $second")
            }

            override fun onFinish() {
                Log.d(TAG,"on finished timer")
                actionTimeout?.cancel()
                delayTimer?.cancel()
                timer?.cancel()
                compositeDisposable?.clear()
                updateBy404()
                viewModel.lastStatus.value = getString(R.string.timeout_order)
            }

        }.start()
    }

    private fun countDownInit(seconds : Int)
    {
        binding.lastStatus.visibility = View.VISIBLE
        binding.stateProgress.visibility = View.VISIBLE
        binding.stateProgress.isIndeterminate = false
        val animator = ObjectAnimator.ofInt(binding.stateProgress,"progress",0,100)
        animator.duration = (seconds * 1000).toLong()
        animator.start()
        timer = object : CountDownTimer((seconds * 1000).toLong(),1000){
            override fun onTick(p0: Long) {
                val remainSecond = (p0 / 1000).toDouble().roundToInt()
                Log.d(TAG, "remains$remainSecond")
                actionTimeout?.cancel()
                if (remainSecond != 0) viewModel.waitOrderTime.value = remainSecond
            }

            override fun onFinish() {
                Log.d(TAG,"onFinish timer")
                viewModel.waitOrderTime.value = 0
                timer?.cancel()
            }

        }.start()
    }

    private fun removeFirstOrder()
    {
        val tmpOrder = userStorage.received_orders
        if (tmpOrder.size > 0)
        {
            tmpOrder.removeAt(0)
        }
        userStorage.received_orders = tmpOrder
        Log.d(TAG,"Orders remains: " + userStorage.received_orders.size)
        myWebViewClient.isJSExecuted = false
        binding.webView.loadUrl("about:blank")
    }

    private fun updateBy404()
    {
        Log.d(TAG,"updateBy404()")
        updateOrder(2,userStorage.received_orders[0].id)
        removeFirstOrder()
    }

    private fun updateOrder(status : Int,order_id:Int)
    {
        
        if (status == 1)
        {
            viewModel.actionErrorCount.value = 0
        }

        if (status != 4 && status !=1)
        {
            val currentErrorValue : Int = viewModel.actionErrorCount.value?:0
            viewModel.actionErrorCount.value = currentErrorValue + 1
        }


        val api = (BFClient(requireActivity())).getClient().create(BFApi::class.java)
        compositeDisposable?.add(api.check(status,order_id).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                profileViewModel.currentCredit.value = it.client
                actionTimeout?.cancel()
                receiveOrders()
            },{
                actionTimeout?.cancel()
                errorHandler(it)
            }))
    }



    override fun onDestroyView() {
        super.onDestroyView()
        stopAllActions()
    }

    private fun stopAllActions()
    {
        binding.webView.destroy()
        compositeDisposable?.clear()
        actionTimeout?.cancel()
        timer?.cancel()
        delayTimer?.cancel()
        myWebViewClient.isJSExecuted = false
        socketConnector?.disconnect()
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
                    view!!.loadUrl(JavascriptInjectors.followInject)

                }else{
                    view!!.loadUrl(JavascriptInjectors.likeInject)
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

    private fun errorHandler(it : Throwable)
    {
        if(userStorage.received_orders.size > 0)
        {
            updateBy404()
        }else{
            receiveOrders()
        }

    }

    override fun onSessionExpired() {
       stopAllActions()
    }


}