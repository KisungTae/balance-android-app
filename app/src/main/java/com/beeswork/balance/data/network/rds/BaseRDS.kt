package com.beeswork.balance.data.network.rds

import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.api.HttpHeader
import com.beeswork.balance.data.network.request.login.RefreshAccessTokenBody
import com.beeswork.balance.data.network.response.ErrorResponse
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.login.RefreshAccessTokenDTO
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.exception.*
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.google.gson.Gson
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.*


abstract class BaseRDS(
    protected val balanceAPI: BalanceAPI,
    protected val preferenceProvider: PreferenceProvider
) {

    protected suspend fun <T> getResult(call: suspend () -> Response<T>): Resource<T> {
        return sendRequest {
            val response = call()
            if (response.isSuccessful) return@sendRequest Resource.success(response.body())
            if (response.headers()[HttpHeader.CONTENT_TYPE] == APPLICATION_XML) return@sendRequest handleXmlException(response)
            else {
                val errorResponse = Gson().fromJson(response.errorBody()?.charStream(), ErrorResponse::class.java)
                if (errorResponse.error == ExceptionCode.EXPIRED_JWT_EXCEPTION) {

                    val beforeRefreshToken = preferenceProvider.getRefreshToken()
                    val refreshAccessTokenResponse = refreshAccessToken()

                    if (refreshAccessTokenResponse.isSuccess()) refreshAccessTokenResponse.data?.let { refreshAccessTokenDTO ->
                        preferenceProvider.putRefreshToken(refreshAccessTokenDTO.refreshToken)
//                        preferenceProvider.putAccessToken(refreshAccessTokenDTO.accessToken)
                        return@sendRequest getResultWithoutRefreshAccessToken(call)
                    } else {
                        val afterRefreshToken = preferenceProvider.getRefreshToken()
                        if (refreshAccessTokenResponse.error == ExceptionCode.REFRESH_TOKEN_KEY_NOT_FOUND_EXCEPTION
                            && !beforeRefreshToken.equals(afterRefreshToken)
                        ) return@sendRequest getResultWithoutRefreshAccessToken(call)
                    }
                }
                return@sendRequest Resource.error(errorResponse.error, errorResponse.message, errorResponse.fieldErrorMessages)
            }
        }
    }

    private suspend fun <T> getResultWithoutRefreshAccessToken(call: suspend () -> Response<T>): Resource<T> {
        return sendRequest {
            val response = call()
            if (response.isSuccessful) Resource.success(response.body())
            else {
                val errorResponse = Gson().fromJson(response.errorBody()?.charStream(), ErrorResponse::class.java)
                validateAccessAndRefreshToken(errorResponse.error, errorResponse.message)
                Resource.error(errorResponse.error, errorResponse.message)
            }
        }
    }


    private suspend fun refreshAccessToken(): Resource<RefreshAccessTokenDTO> {
        return sendRequest {
            val accessToken = preferenceProvider.getAccessToken()
            if (accessToken.isNullOrBlank()) return@sendRequest Resource.error(ExceptionCode.ACCESS_TOKEN_NOT_FOUND_EXCEPTION)

            val refreshToken = preferenceProvider.getRefreshToken()
            if (refreshToken.isNullOrBlank()) return@sendRequest Resource.error(ExceptionCode.REFRESH_TOKEN_NOT_FOUND_EXCEPTION)

            val refreshAccessTokenBody = RefreshAccessTokenBody(accessToken, refreshToken)
            val response = balanceAPI.refreshAccessToken(refreshAccessTokenBody)

            if (response.isSuccessful) return@sendRequest Resource.success(response.body())
            else {
                val errorResponse = Gson().fromJson(response.errorBody()?.charStream(), ErrorResponse::class.java)
                return@sendRequest Resource.error(errorResponse.error, errorResponse.message)
            }
        }
    }

    private fun <T> handleXmlException(response: Response<T>): Resource<T> {
        val factory = XmlPullParserFactory.newInstance()
        val parser = factory.newPullParser()
        parser.setInput(response.errorBody()?.charStream())
        factory.isNamespaceAware = true
        var eventType = parser.eventType
        var error: String? = null
        var message: String? = null

        var text = ""
        while (eventType != XmlPullParser.END_DOCUMENT) {
            val tagName = parser.name
            when (eventType) {
                XmlPullParser.START_TAG -> println()
                XmlPullParser.TEXT -> {
                    text = parser.text
                }
                XmlPullParser.END_TAG -> {
                    if (tagName.toLowerCase(Locale.ROOT) == XML_CODE) error = text
                    if (tagName.toLowerCase(Locale.ROOT) == XML_MESSAGE) message = text
                }
            }
            eventType = parser.next()
        }
        return Resource.error(error, message)
    }

    private fun validateAccessAndRefreshToken(error: String?, errorMessage: String?) {
        when (error) {
            ExceptionCode.INVALID_REFRESH_TOKEN_EXCEPTION -> throw InvalidRefreshTokenException(errorMessage)
            ExceptionCode.EXPIRED_JWT_EXCEPTION -> throw ExpiredJWTException(errorMessage)
        }
    }


    private suspend fun <T> sendRequest(block: suspend () -> Resource<T>): Resource<T> {
        return try {
            block.invoke()
        } catch (e: SocketTimeoutException) {
            Resource.error(ExceptionCode.SOCKET_TIMEOUT_EXCEPTION)
        } catch (e: NoInternetConnectivityException) {
            Resource.error(ExceptionCode.NO_INTERNET_CONNECTIVITY_EXCEPTION)
        } catch (e: ConnectException) {
            Resource.error(ExceptionCode.CONNECT_EXCEPTION)
        } catch (e: UnknownHostException) {
            Resource.error(ExceptionCode.UNKNOWN_HOST_EXCEPTION)
        } catch (e: AccessTokenNotFoundException) {
            Resource.error(ExceptionCode.ACCESS_TOKEN_NOT_FOUND_EXCEPTION)
        }
    }

    companion object {
        private const val XML_CODE = "code"
        private const val XML_MESSAGE = "message"
        private const val APPLICATION_XML = "application/xml"
    }

}
