package com.beeswork.balance.ui.common

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.service.stomp.WebSocketEvent
import com.beeswork.balance.internal.constant.BundleKey
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.exception.BaseException
import com.beeswork.balance.ui.loginactivity.LoginActivity

abstract class BaseActivity : AppCompatActivity() {

//    protected fun validateLogin(exception: Throwable?): Boolean {
//        if (ExceptionCode.isLoginException(exception)) {
//            moveToLoginActivity(exception)
//            return false
//        }
//        return true
//    }
//
//    fun finishToActivity(intent: Intent) {
//        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//        startActivity(intent)
//        this.finish()
//    }
//
//    fun moveToLoginActivity(exception: Throwable?) {
//        val intent = Intent(this, LoginActivity::class.java)
//        if (exception is BaseException) {
//            intent.putExtra(BundleKey.ERROR, exception.error)
//            intent.putExtra(BundleKey.ERROR_MESSAGE, exception.message)
//        }
//        finishToActivity(intent)
//    }
}