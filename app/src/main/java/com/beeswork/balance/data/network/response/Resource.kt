package com.beeswork.balance.data.network.response

import com.beeswork.balance.data.network.response.common.EmptyResponse

class Resource<out T>(
    val status: Status,
    val data: T?,
    val error: String?,
    val errorMessage: String?,
    val fieldErrorMessages: Map<String, String>?
) {

    var hasBeenHandled = false
        private set

    fun getContentIfNotHandledOrReturnNull(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            data
        }
    }

    fun isSuccess(): Boolean {
        return this.status == Status.SUCCESS
    }

    fun isLoading(): Boolean {
        return this.status == Status.LOADING
    }

    fun isError(): Boolean {
        return this.status == Status.ERROR
    }

    fun <P> mapData(data: P?): Resource<P> {
        return Resource(this.status, data, this.errorMessage, this.error, this.fieldErrorMessages)
    }

    fun toEmptyResponse(): Resource<EmptyResponse> {
        return Resource(
            this.status,
            EmptyResponse(),
            this.errorMessage,
            this.error,
            this.fieldErrorMessages
        )
    }

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null, null, null)
        }

        fun <T> error(
            error: String?,
            errorMessage: String?,
            fieldErrorMessages: Map<String, String>?
        ): Resource<T> {
            return Resource(Status.ERROR, null, errorMessage, error, fieldErrorMessages)
        }

        fun <T> error(
            error: String?,
            errorMessage: String?,
        ): Resource<T> {
            return Resource(Status.ERROR, null, errorMessage, error, null)
        }

        fun <T> error(
            error: String,
        ): Resource<T> {
            return Resource(Status.ERROR, null, null, error, null)
        }


        fun <T> loading(): Resource<T> {
            return Resource(Status.LOADING, null, null, null, null)
        }
    }
}