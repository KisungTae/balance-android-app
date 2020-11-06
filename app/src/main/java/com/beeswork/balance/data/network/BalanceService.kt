package com.beeswork.balance.data.network

import com.beeswork.balance.data.database.entity.Click
import com.beeswork.balance.data.database.entity.Clicked
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.network.interceptor.ConnectivityInterceptor
import com.beeswork.balance.data.network.request.ClickRequest
import com.beeswork.balance.data.network.request.FCMTokenRequest
import com.beeswork.balance.data.network.request.SwipeRequest
import com.beeswork.balance.data.network.response.BalanceGameResponse
import com.beeswork.balance.data.network.response.CardResponse
import com.beeswork.balance.data.network.response.ClickResponse
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


    @GET("account/recommend")
    suspend fun fetchCards(
        @Query(value = "accountId") accountId: String,
        @Query(value = "email") email: String,
        @Query(value = "latitude") latitude: Double,
        @Query(value = "longitude") longitude: Double,
        @Query(value = "minAge") minAge: Int,
        @Query(value = "maxAge") maxAge: Int,
        @Query(value = "gender") gender: Boolean,
        @Query(value = "distance") distance: Int
    ): Response<MutableList<CardResponse>>

    @POST("swipe")
    suspend fun swipe(@Body swipeRequest: SwipeRequest): Response<BalanceGameResponse>

    @POST("swipe/click")
    suspend fun click(@Body clickRequest: ClickRequest): Response<ClickResponse>

    @POST("account/fcm/token")
    suspend fun postFCMToken(@Body fcmTokenRequest: FCMTokenRequest): Response<EmptyJsonResponse>

    @GET("match/list")
    suspend fun fetchMatches(
        @Query(value = "accountId") accountId: String,
        @Query(value = "email") email: String,
        @Query(value = "fetchedAt") fetchedAt: String
    ): Response<MutableList<Match>>

    @GET("swipe/clicked/list")
    suspend fun fetchClickedList(
        @Query(value = "accountId") clickedId: String,
        @Query(value = "email") email: String,
        @Query(value = "fetchedAt") fetchedAt: String
    ): Response<MutableList<Clicked>>

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