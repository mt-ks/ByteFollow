package com.fastfollow.bytefollow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.fastfollow.bytefollow.databinding.ActivityAppBinding
import com.fastfollow.bytefollow.storage.UserStorage
import com.fastfollow.bytefollow.ui.profile.ProfileViewModel

class AppActivity : AppCompatActivity() {

    private val viewModel : ProfileViewModel by viewModels()
    private lateinit var binding : ActivityAppBinding
    private lateinit var storage  :UserStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storage = UserStorage(this)
        checkAuth()
        binding = ActivityAppBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initClient()

        val navHostFragment : NavHostFragment = supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
        NavigationUI.setupWithNavController(binding.bottomNavigationView,navHostFragment.navController)
    }

    private fun initClient()
    {
       viewModel.userDetail.value = storage.userDetail
       viewModel.currentCredit.value = storage.credit
       viewModel.currentCredit.observe(this, {
           binding.clientCredit.text = it.toString()
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

}