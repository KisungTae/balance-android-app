package com.beeswork.balance.ui.common

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.beeswork.balance.internal.constant.BundleKey
import com.beeswork.balance.internal.exception.*
import com.beeswork.balance.ui.loginactivity.LoginActivity

abstract class BaseActivity: AppCompatActivity() {

    protected fun observeExceptionLiveData(baseViewModel: BaseViewModel) {
        baseViewModel.exceptionLiveData.observe(this) { exception -> catchException(exception) }
    }

    private fun catchException(throwable: Throwable) {
        when (throwable) {
            is AccountNotFoundException,
            is AccountDeletedException,
            is AccountBlockedException -> moveToLoginActivity(null, throwable.message)
            is InvalidRefreshTokenException,
            is ExpiredJWTException -> moveToLoginActivity(null, null)
            else -> throw throwable
        }
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