package com.fastfollow.bytefollow.ui.login

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.fastfollow.bytefollow.databinding.FragmentLoginBinding
import com.fastfollow.bytefollow.helpers.JsonFieldChecker
import com.fastfollow.bytefollow.service.TKApi
import com.fastfollow.bytefollow.service.TKClient
import com.fastfollow.bytefollow.storage.UserStorage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import java.util.regex.Matcher
import java.util.regex.Pattern

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

                CookieManager.getInstance()
                    .getCookie("https://www.tiktok.com/")
                    ?.let {
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
                    getUserInfo(it.data.username)
                }
            },{

            }))

    }

    private fun getUserInfo(username: String)
    {
        val api = TKClient(requireContext()).getClient().create(TKApi::class.java)
        compositeDisposable?.add(api.getUserInfo(username).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val pattern : Pattern = Pattern.compile(
                    "(.*?)<script id=\"__NEXT_DATA__\" type=\"application/json\"(.*?)>(.*?)</script>(.*?)",
                    Pattern.CASE_INSENSITIVE or Pattern.DOTALL)
                val matcher : Matcher = pattern.matcher(it.string())
                if(matcher.matches() && matcher.groupCount() >= 3)
                {
                    val userData : String = matcher.group(3)?:""
                    val jsonData : JSONObject = JSONObject(userData)
                    if(JsonFieldChecker("props>pageProps>userInfo>user",jsonData).check()){
                        val userInfo = jsonData.getJSONObject("props").getJSONObject("pageProps").getJSONObject("userInfo").getJSONObject("user");
                        Log.d(TAG,"MyDAta: " + userInfo.toString())
                    }
                }
            },{

            }))
    }

    private fun registerDevice()
    {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}