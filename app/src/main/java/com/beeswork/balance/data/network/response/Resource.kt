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
        return Resource(this.status, data, this.error, this.errorMessage, this.fieldErrorMessages)
    }

    fun toEmptyResponse(): Resource<EmptyResponse> {
        return Resource(
            this.status,
            EmptyResponse(),
            this.error,
            this.errorMessage,
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
            return Resource(Status.ERROR, null, error, errorMessage, fieldErrorMessages)
        }

        fun <T> error(
            error: String?,
            errorMessage: String?,
        ): Resource<T> {
            return Resource(Status.ERROR, null, error, errorMessage, null)
        }

        fun <T> error(
            error: String,
        ): Resource<T> {
            return Resource(Status.ERROR, null, error, null, null)
        }


        fun <T> loading(): Resource<T> {
            return Resource(Status.LOADING, null, null, null, null)
        }
    }
}