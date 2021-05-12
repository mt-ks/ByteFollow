package com.fastfollow.bytefollow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.fastfollow.bytefollow.storage.UserStorage

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAuth()
        setContentView(R.layout.activity_main)
    }

    private fun checkAuth()
    {
        val storage = UserStorage(this)
        if (storage.user_id.isNotEmpty())
        {
            val intent = Intent(this,AppActivity::class.java)
            startActivity(intent)
            this.finish()
        }
    }

}