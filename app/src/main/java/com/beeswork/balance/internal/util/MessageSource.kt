package com.beeswork.balance.internal.util

import android.content.Context
import com.beeswork.balance.R
import com.beeswork.balance.internal.exception.BaseException
import com.beeswork.balance.internal.exception.ServerException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class MessageSource {

    companion object {
        fun getMessage(context: Context, exception: Throwable?): String? {
            if (exception == null) {
                return null
            }

            if (exception is ServerException) {
                return exception.message
            }

            val resources = context.resources
            if (exception is BaseException && exception.error != null) {
                val resourceId = resources.getIdentifier(exception.error, "string", context.packageName)
                if (resourceId > 0) {
                    return resources.getString(resourceId)
                }
            }

            return when (exception) {
                is SocketTimeoutException -> {
                    resources.getString(R.string.socket_timeout_exception)
                }
                is ConnectException -> {
                    resources.getString(R.string.connect_exception)
                }
                is UnknownHostException -> {
                    resources.getString(R.string.unknown_host_exception)
                }
                else -> exception.message
            }

        }
    }
}