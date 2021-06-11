package com.kubernet.bytefollow

import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity
import com.kubernet.bytefollow.helpers.ContextUtils
import com.kubernet.bytefollow.storage.UserStorage
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