package com.beeswork.balance.data.network.rds

import com.beeswork.balance.data.network.response.ErrorResponse
import com.beeswork.balance.internal.exception.NoInternetConnectivityException
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.internal.constant.ExceptionCode
import com.google.gson.Gson
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException

abstract class BaseRDS {

    protected suspend fun <T> getResult(call: suspend () -> Response<T>): Resource<T> {
        val response = call()
        val headers = response.headers()

        println("response: $response")

//        Gson().toJson(response.errorBody()?.charStream())

        println("error body: ${Gson().toJson(response.errorBody()?.toString())}")

        if (response.isSuccessful)
            return Resource.success(response.body())

        val errorResponse = Gson().fromJson(
            response.errorBody()?.charStream(),
            ErrorResponse::class.java
        )

        println("errorResponse: $errorResponse")

        return Resource.error(
            errorResponse.message,
            errorResponse.error,
            errorResponse.fieldErrorMessages
        )
//        try {
//            val response = call()
//            val headers = response.headers()
//
//            if (response.isSuccessful)
//                return Resource.success(response.body())
//
//            val errorResponse = Gson().fromJson(
//                response.errorBody()?.charStream(),
//                ErrorResponse::class.java
//            )
//
//            return Resource.error(
//                errorResponse.message,
//                errorResponse.error,
//                errorResponse.fieldErrorMessages
//            )
//
//        } catch (e: SocketTimeoutException) {
//            return Resource.error(ExceptionCode.SOCKET_TIMEOUT_EXCEPTION)
//        } catch (e: NoInternetConnectivityException) {
//            return Resource.error(ExceptionCode.NO_INTERNET_CONNECTIVITY_EXCEPTION)
//        } catch (e: ConnectException) {
//            return Resource.error(ExceptionCode.CONNECT_EXCEPTION)
//        } catch (e: Exception) {
//
//            println("${e.message}")
//            return Resource.error(ExceptionCode.EXCEPTION)
//        }
    }
}
