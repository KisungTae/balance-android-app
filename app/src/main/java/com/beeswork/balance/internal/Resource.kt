package com.beeswork.balance.internal

class Resource<out T>(val status: Status, val data: T?, val exceptionMessage: String?, val exceptionCode: String?) {

    enum class Status {
        SUCCESS,
        EXCEPTION,
        LOADING
    }

    companion object {
        fun <T> success(data: T): Resource<T> {
            return Resource(Status.SUCCESS, data, null, null)
        }

        fun <T> exception(message: String, exceptionCode: String = "", data: T? = null): Resource<T> {

            return Resource(Status.EXCEPTION, data, message, exceptionCode)
        }

        fun <T> loading(data: T? = null): Resource<T> {
            return Resource(Status.LOADING, data, null, null)
        }
    }
}