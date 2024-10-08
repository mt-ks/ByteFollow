package com.kubernet.bytefollow.helpers

import android.app.Activity
import com.kubernet.bytefollow.R
import com.kubernet.bytefollow.dialogs.LogoutDialog
import com.kubernet.bytefollow.service.TKApi
import com.kubernet.bytefollow.service.TKClient
import com.kubernet.bytefollow.storage.UserStorage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SessionChecker(val activity: Activity, private val sessionInterface: SessionInterface) {


    init {
        checkSession()
    }

    private fun checkSession()
    {
        val compositeDisposable = CompositeDisposable()
        val userStorage = UserStorage(activity)
        val cookieString = userStorage.cookie
        val api = TKClient(activity).setDynamicCookie(cookieString).getClient().create(TKApi::class.java)
        compositeDisposable.add(api.checkCookie().subscribeOn(Schedulers.io()).observeOn(
            AndroidSchedulers.mainThread())
            .subscribe ({
                if(it.message == "success")
                {
                    userStorage.cookie = cookieString
                }
                if (it.data.name == "session_expired")
                {
                    sessionInterface.onSessionExpired()
                    val logoutDialog : LogoutDialog = LogoutDialog(activity)
                    logoutDialog.customTitle = activity.getString(R.string.youre_logout)
                    logoutDialog.customMessage = activity.getString(R.string.session_expired_desc)
                    logoutDialog.start()
                }

            },{
                it.printStackTrace()
            }))
    }


}