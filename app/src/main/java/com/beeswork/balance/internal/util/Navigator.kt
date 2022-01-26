package com.beeswork.balance.internal.util

import android.app.Activity
import android.content.Intent
import com.beeswork.balance.internal.constant.BundleKey
import com.beeswork.balance.internal.exception.BaseException
import com.beeswork.balance.ui.loginactivity.LoginActivity

class Navigator {

    companion object {
        fun finishToActivity(fromActivity: Activity, intent: Intent) {
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            fromActivity.startActivity(intent)
            fromActivity.finish()
        }

        fun finishToLoginActivity(fromActivity: Activity, message: String?) {
            val intent = Intent(fromActivity, LoginActivity::class.java)
            if (message != null) {
                intent.putExtra(BundleKey.ERROR_MESSAGE, message)
            }
            finishToActivity(fromActivity, intent)
        }
    }
}