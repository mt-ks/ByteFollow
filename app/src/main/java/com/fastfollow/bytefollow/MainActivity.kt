package com.fastfollow.bytefollow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import com.fastfollow.bytefollow.databinding.ActivityMainBinding
import com.fastfollow.bytefollow.storage.UserStorage
import com.fastfollow.bytefollow.ui.profile.ProfileViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAuth()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun checkAuth()
    {
        val storage = UserStorage(this)
        if (storage.userId.isNotEmpty())
        {
            val intent = Intent(this,AppActivity::class.java)
            startActivity(intent)
            this.finish()
        }
    }

}