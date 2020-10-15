package com.beeswork.balance.data.network

import com.beeswork.balance.data.entity.Click
import com.beeswork.balance.data.entity.Match
import com.beeswork.balance.data.network.interceptor.ConnectivityInterceptor
import com.beeswork.balance.data.network.request.FCMTokenRequest
import com.beeswork.balance.data.network.request.SwipeRequest
import com.beeswork.balance.data.network.response.BalanceGame
import com.beeswork.balance.data.network.response.Card
import com.beeswork.balance.data.network.response.EmptyJsonResponse
import com.beeswork.balance.internal.constant.NETWORK_CONNECTION_TIMEOUT
import com.beeswork.balance.internal.constant.NETWORK_READ_TIMEOUT
import com.beeswork.balance.internal.converter.StringToOffsetDateTimeDeserializer
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import org.threeten.bp.OffsetDateTime
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
    suspend fun fetchCards(
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

    @POST("account/fcm/token/save")
    suspend fun postFCMToken(@Body fcmTokenRequest: FCMTokenRequest): Response<EmptyJsonResponse>

    @GET("match/list")
    suspend fun fetchMatches(
        @Query(value = "matcherId") matcherId: String,
        @Query(value = "fetchedAt") fetchedAt: String
    ): Response<MutableList<Match>>

    companion object {
        operator fun invoke(
            connectivityInterceptor: ConnectivityInterceptor
        ): BalanceService {

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(connectivityInterceptor)
                .readTimeout(NETWORK_READ_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(NETWORK_CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .build();

            val gson = GsonBuilder().registerTypeAdapter(
                OffsetDateTime::class.java,
                StringToOffsetDateTimeDeserializer()
            ).create()

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("http://10.0.2.2:8080/")
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(BalanceService::class.java)
        }
    }
}