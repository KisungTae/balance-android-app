package com.beeswork.balance.internal.util

import com.beeswork.balance.App
import com.beeswork.balance.R
import com.beeswork.balance.internal.exception.BaseException
import com.beeswork.balance.internal.exception.ServerException
import java.lang.StringBuilder
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class MessageSource {

    companion object {

        fun getMessageFromFieldErrors(exception: Throwable?): String? {
            if (exception !is ServerException || exception.fieldErrors.isNullOrEmpty()) {
                return null
            }
            val stringBuilder = StringBuilder()
            exception.fieldErrors.forEach { entry ->
                stringBuilder.append(entry.value)
                stringBuilder.append("\n")
            }
            stringBuilder.deleteCharAt(stringBuilder.length - 1)
            return stringBuilder.toString()
        }

        fun getMessage(exception: Throwable?): String? {
            if (exception == null) {
                return null
            }

            if (exception is ServerException) {
                return exception.message
            }

            val resources = App.getContext()?.resources
            if (exception is BaseException && exception.error != null) {
                val resourceId = resources?.getIdentifier(exception.error, "string", App.getContext()?.packageName)
                if (resourceId != null && resourceId > 0) {
                    return resources.getString(resourceId)
                }
            }

            return when (exception) {
                is SocketTimeoutException -> {
                    resources?.getString(R.string.socket_timeout_exception)
                }
                is ConnectException -> {
                    resources?.getString(R.string.connect_exception)
                }
                is UnknownHostException -> {
                    resources?.getString(R.string.unknown_host_exception)
                }
                else -> exception.message
            }

        }
    }
}