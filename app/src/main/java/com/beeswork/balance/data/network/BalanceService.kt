package com.beeswork.balance.data.network

import com.beeswork.balance.data.entity.Click
import com.beeswork.balance.data.network.response.Card
import com.beeswork.balance.data.network.interceptor.ConnectivityInterceptor
import com.beeswork.balance.data.network.request.FirebaseMessagingTokenRequest
import com.beeswork.balance.data.network.request.SwipeRequest
import com.beeswork.balance.data.network.response.BalanceGame
import com.beeswork.balance.data.network.response.EmptyJsonResponse
import com.beeswork.balance.internal.constant.NetworkConstant
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


interface BalanceService {


    @GET("recommend")
    suspend fun fetchCardsAsync(
        @Query(value = "account_id") accountId: String,
        @Query(value = "latitude") latitude: Double,
        @Query(value = "longitude") longitude: Double,
        @Query(value = "min_age") minAge: Int,
        @Query(value = "max_age") maxAge: Int,
        @Query(value = "gender") gender: Boolean,
        @Query(value = "distance") distance: Int
    ): Response<MutableList<Card>>

    @POST("swipe")
    suspend fun swipe(@Body swipeRequest: SwipeRequest): Response<BalanceGame>

    @POST("click")
    suspend fun click(@Body swipeRequest: SwipeRequest): Response<Click>

    @POST("account/message-token/save")
    suspend fun postFirebaseMessagingToken(@Body firebaseMessagingTokenRequest: FirebaseMessagingTokenRequest): Response<EmptyJsonResponse>

    companion object {
        operator fun invoke(
            connectivityInterceptor: ConnectivityInterceptor
        ): BalanceService {

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(connectivityInterceptor)
                .readTimeout(NetworkConstant.READ_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(NetworkConstant.CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .build();

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("http://10.0.2.2:8080/")
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BalanceService::class.java)
        }
    }
}