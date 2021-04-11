package com.beeswork.balance.internal.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.lifecycle.MutableLiveData
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.exception.AccountIdNotFoundException
import com.beeswork.balance.internal.exception.IdentityTokenNotFoundException
import com.beeswork.balance.internal.exception.NoInternetConnectivityException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun <T1 : Any, T2 : Any, R : Any> safeLet(p1: T1?, p2: T2?, block: (T1, T2) -> R?): R? {
    return if (p1 != null && p2 != null) block(p1, p2) else null
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

fun <T> CoroutineScope.safeLaunch(callBack: MutableLiveData<Resource<T>>?, launchBody: suspend () -> Unit): Job {
    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        when (throwable) {
            is SocketTimeoutException -> callBack?.postValue(Resource.error(ExceptionCode.SOCKET_TIMEOUT_EXCEPTION))
            is NoInternetConnectivityException -> callBack?.postValue(Resource.error(ExceptionCode.NO_INTERNET_CONNECTIVITY_EXCEPTION))
            is ConnectException -> callBack?.postValue(Resource.error(ExceptionCode.CONNECT_EXCEPTION))
            else -> throw throwable
        }
    }

    return this.launch(coroutineExceptionHandler) {
        launchBody.invoke()
    }
}


