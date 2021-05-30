package com.fastfollow.bytefollow

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.fastfollow.bytefollow.databinding.ActivityAppBinding
import com.fastfollow.bytefollow.helpers.SocketConnector
import com.fastfollow.bytefollow.storage.UserStorage
import com.fastfollow.bytefollow.ui.profile.ProfileViewModel
import com.onesignal.OneSignal

class AppActivity : BaseActivity() {

    private val viewModel : ProfileViewModel by viewModels()
    private lateinit var binding : ActivityAppBinding
    private lateinit var storage  :UserStorage
    private var socketConnector: SocketConnector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        storage = UserStorage(this)
        checkAuth()
        binding = ActivityAppBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initClient()
        socketConnector = SocketConnector(this,"DEFAULT")
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
        OneSignal.initWithContext(this)
        OneSignal.setAppId(BuildConfig.ONEHASHKEY)
        val navHostFragment : NavHostFragment = supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
        NavigationUI.setupWithNavController(binding.bottomNavigationView,navHostFragment.navController)
    }

    private fun initClient()
    {
       viewModel.userDetail.value = storage.userDetail
       viewModel.currentCredit.value = storage.credit
       viewModel.currentCredit.observe(this, {
           binding.clientCredit.text = it.toString()
           storage.credit = it
       })

    }

    private fun checkAuth()
    {
        if (storage.userId.isEmpty())
        {
            val intent = Intent(this,AppActivity::class.java)
            startActivity(intent)
            this.finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        socketConnector?.disconnect()
    }
}