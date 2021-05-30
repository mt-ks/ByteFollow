package com.fastfollow.bytefollow

import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity
import com.fastfollow.bytefollow.helpers.ContextUtils
import com.fastfollow.bytefollow.storage.UserStorage
import com.google.firebase.FirebaseApp
import java.util.*

open class BaseActivity : AppCompatActivity() {



    override fun attachBaseContext(newBase: Context) {
        val userStorage : UserStorage = UserStorage(newBase)
        var customLanguage = userStorage.customLanguage
        if (customLanguage.toLowerCase(Locale.ROOT) != "tr" && customLanguage.toLowerCase(Locale.ROOT) != "en")
        {
            customLanguage = "en"
        }
        val localeUpdatedContext: ContextWrapper = ContextUtils.updateLocale(newBase, Locale(customLanguage))
        super.attachBaseContext(localeUpdatedContext)
    }

}