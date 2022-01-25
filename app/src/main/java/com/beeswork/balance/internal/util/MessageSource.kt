package com.beeswork.balance.internal.util

import android.content.Context
import android.content.res.Resources
import com.beeswork.balance.R
import com.beeswork.balance.internal.exception.BaseException
import com.beeswork.balance.internal.exception.ServerException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class MessageSource {

    companion object {
        fun getMessage(context: Context, resources: Resources, throwable: Throwable?): String? {
            if (throwable == null) {
                return null
            }

            if (throwable is ServerException) {
                return throwable.message
            }

            if (throwable is BaseException && throwable.code != null) {
                val resourceId = resources.getIdentifier(throwable.code, "string", context.packageName)
                if (resourceId > 0) {
                    return resources.getString(resourceId)
                }
            }

            return when (throwable) {
                is SocketTimeoutException -> {
                    resources.getString(R.string.socket_timeout_exception)
                }
                is ConnectException -> {
                    resources.getString(R.string.connect_exception)
                }
                is UnknownHostException -> {
                    resources.getString(R.string.unknown_host_exception)
                }
                else -> null
            }

        }
    }
}