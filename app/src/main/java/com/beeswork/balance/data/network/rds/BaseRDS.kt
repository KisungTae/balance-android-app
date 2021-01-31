package com.beeswork.balance.data.network.rds

import com.beeswork.balance.data.network.response.ExceptionResponse
import com.beeswork.balance.internal.exception.NoInternetConnectivityException
import com.beeswork.balance.data.observable.Resource
import com.beeswork.balance.internal.constant.ExceptionCode
import com.google.gson.Gson
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

            return Resource.error(
                exceptionResponse.message,
                exceptionResponse.error,
                exceptionResponse.fieldErrorMessages
            )

        } catch (e: SocketTimeoutException) {
            return Resource.error(
                e.message ?: "",
                ExceptionCode.SOCKET_TIMEOUT_EXCEPTION
            )
        } catch (e: NoInternetConnectivityException) {
            return Resource.error(
                e.message ?: "",
                ExceptionCode.NO_INTERNET_CONNECTIVITY_EXCEPTION
            )
        } catch (e: Exception) {
            return Resource.error(
                e.message ?: "",
                ExceptionCode.EXCEPTION
            )
        }
    }
}
