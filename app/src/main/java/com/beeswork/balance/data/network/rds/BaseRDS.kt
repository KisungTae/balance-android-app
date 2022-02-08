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
            if (response.isSuccessful) {
                return@sendRequest Resource.success(response.body())
            }
            if (response.headers()[HttpHeader.CONTENT_TYPE] == APPLICATION_XML) {
                println("getResult xml")
                return@sendRequest handleXmlException(response)
            } else {
                println("getResult else")
                val errorResponse = convertToErrorResponse(response)
                if (!errorResponse.isExceptionEqualTo(ExceptionCode.EXPIRED_JWT_EXCEPTION)) {
                    return@sendRequest errorResponse
                }
                println("after getResult convertoErrorResponse")
                val refreshAccessTokenResponse = doRefreshAccessToken()
                if (refreshAccessTokenResponse.isSuccess()) {
                    return@sendRequest getResultWithoutRefreshAccessToken(call)
                }
                return@sendRequest refreshAccessTokenResponse.map { null }
            }
        }
    }

    private suspend fun <T> getResultWithoutRefreshAccessToken(call: suspend () -> Response<T>): Resource<T> {
        return sendRequest {
            val response = call()
            if (response.isSuccessful) Resource.success(response.body())
            else convertToErrorResponse(response)
        }
    }

    private fun <T> convertToErrorResponse(response: Response<T>): Resource<T> {
        val errorResponse = Gson().fromJson(response.errorBody()?.charStream(), ErrorResponse::class.java)
        println(errorResponse.error)
        println(errorResponse.message)
        val serverException = ServerException(errorResponse.error, errorResponse.message, errorResponse.fieldErrorMessages)
        return Resource.error(serverException)
    }


    protected suspend fun doRefreshAccessToken(): Resource<RefreshAccessTokenDTO> {
        return sendRequest {
            val accessToken = preferenceProvider.getAccessToken()
            if (accessToken.isNullOrBlank()) {
                return@sendRequest Resource.error(AccessTokenNotFoundException())
            }

            val refreshToken = preferenceProvider.getRefreshToken()
            if (refreshToken.isNullOrBlank()) {
                return@sendRequest Resource.error(RefreshTokenNotFoundException())
            }

            val refreshAccessTokenBody = RefreshAccessTokenBody(accessToken, refreshToken)
            val response = balanceAPI.refreshAccessToken(refreshAccessTokenBody)

            return@sendRequest if (response.isSuccessful) {
                val resource = Resource.success(response.body())
                resource.data?.let { refreshAccessTokenDTO ->
                    preferenceProvider.putLoginInfo(null, refreshAccessTokenDTO.accessToken, refreshAccessTokenDTO.refreshToken)
                }
                resource
            } else {
                convertToErrorResponse(response)
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
        return Resource.error(ServerException(error, message))
    }

    private suspend fun <T> sendRequest(block: suspend () -> Resource<T>): Resource<T> {
        return try {
            block.invoke()
        } catch (e: SocketTimeoutException) {
            Resource.error(e)
        } catch (e: NoInternetConnectivityException) {
            Resource.error(e)
        } catch (e: ConnectException) {
            Resource.error(e)
        } catch (e: UnknownHostException) {
            Resource.error(e)
        } catch (e: AccessTokenNotFoundException) {
            Resource.error(e)
        }
    }

    companion object {
        private const val XML_CODE = "code"
        private const val XML_MESSAGE = "message"
        private const val APPLICATION_XML = "application/xml"
    }

}
