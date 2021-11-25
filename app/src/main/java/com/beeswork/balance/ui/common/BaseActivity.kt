package com.beeswork.balance.ui.common

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.internal.constant.BundleKey
import com.beeswork.balance.internal.exception.*
import com.beeswork.balance.internal.util.Validator
import com.beeswork.balance.ui.loginactivity.LoginActivity
import com.beeswork.balance.ui.mainactivity.MainActivity

abstract class BaseActivity : AppCompatActivity() {

    fun finishToActivity(intent: Intent) {
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        this.finish()
    }

    fun moveToLoginActivity(error: String?, errorMessage: String?) {
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra(BundleKey.ERROR, error)
        intent.putExtra(BundleKey.ERROR_MESSAGE, errorMessage)
        finishToActivity(intent)
    }
}