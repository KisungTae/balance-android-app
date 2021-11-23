package com.beeswork.balance.data.network.interceptor

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.beeswork.balance.data.network.api.HttpHeader
import com.beeswork.balance.internal.exception.NoInternetConnectivityException
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import okhttp3.Interceptor
import okhttp3.Response

class ConnectivityInterceptorImpl(
    context: Context,
    private val preferenceProvider: PreferenceProvider
) : ConnectivityInterceptor {

    private val appContext = context.applicationContext

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isOnline()) throw NoInternetConnectivityException()

        var request = chain.request()
        val noAuthentication = request.header(HttpHeader.NO_AUTHENTICATION).toBoolean()
        if (!noAuthentication) {
            request = chain.request()
                .newBuilder()
                .addHeader(HttpHeader.ACCESS_TOKEN, preferenceProvider.getAccessTokenOrThrow())
                .addHeader(HttpHeader.IDENTITY_TOKEN, preferenceProvider.getIdentityTokenOrThrow().toString())
                .build()
        }
        return chain.proceed(request)
    }

    private fun isOnline(): Boolean {
        val connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    }
}