package com.fastfollow.bytefollow

import android.content.Intent
import android.os.Bundle
import com.fastfollow.bytefollow.databinding.ActivityMainBinding
import com.fastfollow.bytefollow.storage.UserStorage
import com.google.firebase.FirebaseApp

class MainActivity : BaseActivity() {

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