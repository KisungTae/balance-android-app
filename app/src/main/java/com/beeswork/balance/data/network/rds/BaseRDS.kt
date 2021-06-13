package com.beeswork.balance.data.network.rds

import com.beeswork.balance.data.network.response.ErrorResponse
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.exception.NoInternetConnectivityException
import com.google.gson.Gson
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.*


abstract class BaseRDS {

    protected suspend fun <T> getResult(call: suspend () -> Response<T>): Resource<T> {
        try {
//            val headers = response.headers()
            val response = call()

            if (response.isSuccessful)
                return Resource.success(response.body())

            if (response.headers()["Content-Type"] == "application/xml") {
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
                            if (tagName.toLowerCase(Locale.ROOT) == "code") error = text
                            if (tagName.toLowerCase(Locale.ROOT) == "message") message = text
                        }
                    }
                    eventType = parser.next()
                }
                return Resource.error(error, message)
            } else {
                val errorResponse = Gson().fromJson(response.errorBody()?.charStream(), ErrorResponse::class.java)
                return Resource.error(errorResponse.error, errorResponse.message, errorResponse.fieldErrorMessages)
            }

        } catch (e: SocketTimeoutException) {
            return Resource.error(ExceptionCode.SOCKET_TIMEOUT_EXCEPTION)
        } catch (e: NoInternetConnectivityException) {
            return Resource.error(ExceptionCode.NO_INTERNET_CONNECTIVITY_EXCEPTION)
        } catch (e: ConnectException) {
            return Resource.error(ExceptionCode.CONNECT_EXCEPTION)
        } catch (e: UnknownHostException) {
            return Resource.error(ExceptionCode.UNKNOWN_HOST_EXCEPTION)
        }
    }


}
