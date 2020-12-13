package com.beeswork.balance.data.network.rds

import com.beeswork.balance.data.network.response.ExceptionResponse
import com.beeswork.balance.internal.NoInternetConnectivityException
import com.beeswork.balance.internal.Resource
import com.beeswork.balance.internal.constant.ExceptionCode
import com.google.gson.Gson
import kotlinx.coroutines.*
import retrofit2.Response
import java.net.SocketTimeoutException

abstract class BaseRDS {

    protected suspend fun <T> getResult(call: suspend () -> Response<T>): Resource<T> {
        try {

            val response = call()

            if (response.isSuccessful)
                return Resource.success(response.body())

            val exceptionResponse =
                Gson().fromJson(response.errorBody()?.string(), ExceptionResponse::class.java)

            return Resource.exception(
                exceptionResponse.message,
                exceptionResponse.error,
                exceptionResponse.fieldErrorMessages
            )

        } catch (e: SocketTimeoutException) {
            return Resource.exception(
                e.message ?: "",
                ExceptionCode.SOCKET_TIMEOUT_EXCEPTION
            )
        } catch (e: NoInternetConnectivityException) {
            return Resource.exception(
                e.message ?: "",
                ExceptionCode.NO_INTERNET_CONNECTIVITY_EXCEPTION
            )
        } catch (e: Exception) {
            return Resource.exception(
                e.message ?: "",
                ExceptionCode.EXCEPTION
            )
        }
    }
}
