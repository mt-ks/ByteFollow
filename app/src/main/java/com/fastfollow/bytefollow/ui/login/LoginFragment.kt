package com.fastfollow.bytefollow.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.fastfollow.bytefollow.AppActivity
import com.fastfollow.bytefollow.databinding.FragmentLoginBinding
import com.fastfollow.bytefollow.dialogs.LoadingDialog
import com.fastfollow.bytefollow.helpers.UserRequireChecker
import com.fastfollow.bytefollow.model.UserDetail
import com.fastfollow.bytefollow.service.BFApi
import com.fastfollow.bytefollow.service.BFClient
import com.fastfollow.bytefollow.service.TKApi
import com.fastfollow.bytefollow.service.TKClient
import com.fastfollow.bytefollow.storage.UserStorage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private var isAuthenticated = false
    private var compositeDisposable : CompositeDisposable?= null
    private lateinit var userStorage : UserStorage
    private lateinit var loadingDialog : LoadingDialog
    private lateinit var webView : WebView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        userStorage = UserStorage(requireContext())
        CookieManager.getInstance().removeAllCookies(null)
        compositeDisposable = CompositeDisposable()
        loadingDialog = LoadingDialog(requireActivity())
        return _binding!!.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView = binding.webView
        webView.settings.domStorageEnabled = true
        webView.settings.javaScriptEnabled = true
        webView.loadUrl("https://www.tiktok.com/login")
        webView.webViewClient  = object :  WebViewClient() {
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
        if (isAuthenticated){ return }
        loadingDialog.start()
        val api = TKClient(requireActivity()).setDynamicCookie(cookieString).getClient().create(TKApi::class.java)
        compositeDisposable?.add(api.checkCookie().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                if(it.message == "success")
                {
                    isAuthenticated = true
                    userStorage.cookie = cookieString
                    getUserInfo(it.data.username, cookieString)
                }else{
                    loadingDialog.stop()
                }
            },{
                this.errorHandler(it)
            }))

    }

    private fun getUserInfo(username: String, cookieString: String)
    {
        val api = TKClient(requireActivity()).getClient().create(TKApi::class.java)
        compositeDisposable?.add(api.getUserInfo(username).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val check = UserRequireChecker(it)
                if (check.checkUser())
                {
                    registerDevice(check.userDetail, cookieString)
                }else{
                    loadingDialog.stop()
                }

            },{
                this.errorHandler(it)
            }))
    }

    private fun registerDevice(userDetail : UserDetail, cookieString: String)
    {
        val api = BFClient(requireActivity()).getClient().create(BFApi::class.java)
        compositeDisposable?.add(api.register(userDetail.user.uniqueId).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                userStorage.userDetail = userDetail
                userStorage.userId = userDetail.user.id
                userStorage.cookie = cookieString
                userStorage.username = userDetail.user.uniqueId
                userStorage.meInfo = it.info
                userStorage.credit = it.info.credit
                val goProfile = Intent(activity,AppActivity::class.java)
                startActivity(goProfile)
                loadingDialog.stop()
                activity?.finish()
            },{
                this.errorHandler(it)

            }))
    }

    private fun errorHandler(it : Throwable)
    {
        Toast.makeText(context,"An error occurred",Toast.LENGTH_LONG).show()
        it.printStackTrace()
        loadingDialog.stop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        compositeDisposable?.clear()
        removeWebView()
    }

    private fun removeWebView()
    {
        webView.destroy()
    }

}