package com.fastfollow.bytefollow.ui.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.fastfollow.bytefollow.databinding.FragmentLoginBinding
import com.fastfollow.bytefollow.helpers.UserRequireChecker
import com.fastfollow.bytefollow.model.UserInfo
import com.fastfollow.bytefollow.service.BFApi
import com.fastfollow.bytefollow.service.BFClient
import com.fastfollow.bytefollow.service.TKApi
import com.fastfollow.bytefollow.service.TKClient
import com.fastfollow.bytefollow.storage.UserStorage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class LoginFragment : Fragment() {
    private val TAG : String = "LoginFragment"
    private val viewModel : LoginViewModel by activityViewModels()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private var isAuthenticated = false;
    private var compositeDisposable : CompositeDisposable?= null
    private lateinit var userStorage : UserStorage

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        userStorage = UserStorage(requireContext())
        CookieManager.getInstance().removeAllCookies(null)
        compositeDisposable = CompositeDisposable()
        return _binding!!.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val webview = binding.webView;
        webview.settings.domStorageEnabled = true
        webview.settings.javaScriptEnabled = true
        webview.loadUrl("https://www.tiktok.com/login")
        webview.webViewClient  = object :  WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                CookieManager.getInstance().getCookie("https://www.tiktok.com/")?.let {
                        checkAuthenticated(it)
                }
            }
        }
    }

    private fun checkAuthenticated(cookieString : String)
    {
        val api = TKClient(requireContext()).setDynamicCookie(cookieString).getClient().create(TKApi::class.java)
        compositeDisposable?.add(api.checkCookie().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                if(it.message == "success")
                {
                    userStorage.cookie = cookieString
                    getUserInfo(it.data.username, cookieString)
                }
            },{
                this.errorHandler(it)
            }))

    }

    private fun getUserInfo(username: String, cookieString: String)
    {
        if (isAuthenticated){ return }
        val api = TKClient(requireContext()).getClient().create(TKApi::class.java)
        compositeDisposable?.add(api.getUserInfo(username).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val check = UserRequireChecker(it)
                if (check.checkUser())
                {
                    isAuthenticated = true;
                    userStorage.user_info = check.userInfo
                    userStorage.user_id = check.userInfo.id
                    userStorage.cookie = cookieString
                    userStorage.username = check.userInfo.uniqueId
                    registerDevice(check.userInfo)
                }

            },{
                this.errorHandler(it)
            }))
    }

    private fun registerDevice(userInfo : UserInfo)
    {
        val api = BFClient(requireContext()).getClient().create(BFApi::class.java)
        compositeDisposable?.add(api.register(userInfo.uniqueId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({

            },{
                this.errorHandler(it)
            }))
    }

    private fun errorHandler(it : Throwable)
    {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        compositeDisposable?.clear()
    }

}