package com.beeswork.balance.data.network.response

import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.exception.BaseException

class Resource<out T>(
    val status: Status,
    val data: T?,
    val exception: Throwable?
) {
    fun isSuccess(): Boolean {
        return this.status == Status.SUCCESS
    }

    fun isLoading(): Boolean {
        return this.status == Status.LOADING
    }

    fun isError(): Boolean {
        return this.status == Status.ERROR
    }

    fun isExceptionCodeEqualTo(error: String?): Boolean {
        return if (exception is BaseException) {
            exception.error == error
        } else {
            false
        }
    }

    fun <R> map(block: (T?) -> R?): Resource<R> {
        val newData = block.invoke(this.data)
        return Resource(this.status, newData, this.exception)
    }

    fun toEmptyResponse(): Resource<EmptyResponse> {
        return Resource(this.status, EmptyResponse(), this.exception)
    }

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }

//        fun <T> error(error: String?, message: String?, fieldErrors: Map<String, String>?): Resource<T> {
//            return Resource(Status.ERROR, null, ServerException(error, message, fieldErrors))
//        }
//
//        fun <T> error(error: String?, message: String?): Resource<T> {
//            return Resource(Status.ERROR, null, ServerException(error, message, null))
//        }
//
//        fun <T> error(error: String): Resource<T> {
//            return Resource(Status.ERROR, null, ServerException(error, null, null))
//        }

        fun <T> error(exception: Throwable?): Resource<T> {
            return Resource(Status.ERROR, null, exception)
        }

        fun <T> loading(): Resource<T> {
            return Resource(Status.LOADING, null, null)
        }


    }
}