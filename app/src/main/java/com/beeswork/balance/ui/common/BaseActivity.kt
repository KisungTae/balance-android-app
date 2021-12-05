package com.beeswork.balance.ui.common

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.service.stomp.WebSocketEvent
import com.beeswork.balance.internal.constant.BundleKey
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.ui.loginactivity.LoginActivity

abstract class BaseActivity : AppCompatActivity() {

    protected fun validateLogin(webSocketEvent: WebSocketEvent): Boolean {
        return validateLogin(webSocketEvent.error, webSocketEvent.errorMessage)
    }

    private fun validateLogin(error: String?, errorMessage: String?): Boolean {
        if (ExceptionCode.isLoginException(error)) {
            moveToLoginActivity(error, errorMessage)
            return false
        }
        return true
    }

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