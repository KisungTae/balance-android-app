package com.beeswork.balance.internal

class Resource<out T>(
    val status: Status,
    val data: T?,
    val exceptionMessage: String?,
    val exceptionCode: String?,
    val fieldErrorMessages: Map<String, String>?
) {

    enum class Status {
        SUCCESS,
        EXCEPTION,
        LOADING
    }

    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null, null, null)
        }

        fun <T> exception(
            exceptionMessage: String,
            exceptionCode: String,
            fieldErrorMessages: Map<String, String>?
        ): Resource<T> {

            return Resource(Status.EXCEPTION, null, exceptionMessage, exceptionCode, fieldErrorMessages)
        }

        fun <T> loading(): Resource<T> {
            return Resource(Status.LOADING, null, null, null, null)
        }
    }
}