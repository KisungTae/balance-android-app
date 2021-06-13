package com.beeswork.balance.internal.util

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.lifecycle.MutableLiveData
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.exception.AccountIdNotFoundException
import com.beeswork.balance.internal.exception.IdentityTokenNotFoundException
import com.beeswork.balance.internal.exception.NoInternetConnectivityException
import com.beeswork.balance.ui.dialog.ReportDialog
import kotlinx.coroutines.*
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun <T1 : Any, T2 : Any, R : Any> safeLet(p1: T1?, p2: T2?, block: (T1, T2) -> R?): R? {
    return if (p1 != null && p2 != null) block(p1, p2) else null
}

fun <T1 : Any, T2 : Any, T3 : Any, R : Any> safeLet(p1: T1?, p2: T2?, p3: T3?, block: (T1, T2, T3) -> R?): R? {
    return if (p1 != null && p2 != null && p3 != null) block(p1, p2, p3) else null
}

fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, R : Any> safeLet(
    p1: T1?,
    p2: T2?,
    p3: T3?,
    p4: T4?,
    block: (T1, T2, T3, T4) -> R?
): R? {
    return if (p1 != null && p2 != null && p3 != null && p4 != null) block(p1, p2, p3, p4) else null
}


fun <T> CoroutineScope.safeLaunch(
    callBack: MutableLiveData<Resource<T>>?,
    finallyBody: (() -> Unit)? = null,
    launchBody: suspend () -> Unit
): Job {
    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        when (throwable) {
            is SocketTimeoutException -> callBack?.postValue(Resource.error(ExceptionCode.SOCKET_TIMEOUT_EXCEPTION))
            is NoInternetConnectivityException -> callBack?.postValue(Resource.error(ExceptionCode.NO_INTERNET_CONNECTIVITY_EXCEPTION))
            is ConnectException -> callBack?.postValue(Resource.error(ExceptionCode.CONNECT_EXCEPTION))
            is UnknownHostException -> {
                println("UnknownHostException thronw")
                callBack?.postValue(Resource.error(ExceptionCode.UNKNOWN_HOST_EXCEPTION))
            }
            else -> throw throwable
        }
        finallyBody?.invoke()
    }

    return this.launch(coroutineExceptionHandler) {
        launchBody.invoke()
    }
}



fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

const val ANIMATION_DURATION = 100L

fun View.slideInFromLeft() {
    this.translationX = this.width.toFloat().unaryMinus()
    this.visibility = View.VISIBLE
    this.animate()
        .setDuration(ANIMATION_DURATION)
        .translationX(0f)
        .start()
}

fun View.slideInFromRight() {
    this.translationX = this.width.toFloat()
    this.visibility = View.VISIBLE
    this.animate()
        .setDuration(ANIMATION_DURATION)
        .translationX(0f)
        .start()
}

fun View.slideOutToRight() {
    this.animate()
        .withStartAction { this.translationX = 0f }
        .withEndAction { this.visibility = View.GONE }
        .setDuration(ANIMATION_DURATION)
        .translationX(this.width.toFloat())
        .start()
}

fun View.slideOutToLeft() {
    this.animate()
        .withStartAction { this.translationX = 0f }
        .withEndAction { this.visibility = View.GONE }
        .setDuration(ANIMATION_DURATION)
        .translationX(this.width.toFloat().unaryMinus())
        .start()
}

fun View.getText(): String {
    return when (this) {
        is Button -> this.text.toString()
        is TextView -> this.text.toString()
        else -> ""
    }
}

fun <T> lazyDeferred(block: suspend CoroutineScope.() -> T): Lazy<Deferred<T>> {

    return lazy {
        GlobalScope.async(start = CoroutineStart.LAZY) {
            block.invoke(this)
        }
    }
}


fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()