package com.beeswork.balance.data.network.api

import com.beeswork.balance.data.database.entity.Clicked
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.database.entity.Photo
import com.beeswork.balance.data.network.interceptor.ConnectivityInterceptor
import com.beeswork.balance.data.network.request.*
import com.beeswork.balance.data.network.response.*
import com.beeswork.balance.internal.converter.StringToOffsetDateTimeDeserializer
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.threeten.bp.OffsetDateTime
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

//const val NETWORK_READ_TIMEOUT = 100L
//const val NETWORK_CONNECTION_TIMEOUT = 100L

interface BalanceAPI {

    @POST
    @Multipart
//    @Headers(value = ["Content-Type: multipart/form-data"])
    suspend fun uploadPhotoToS3(
        @Url url: String,
        @PartMap formData: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part photo: MultipartBody.Part
    ): Response<ResponseBody>

    @POST("photo/presigned-url")
    suspend fun fetchPreSignedUrl(
        @Body fetchPreSignedUrlRequest: FetchPreSignedUrlRequest
    ): Response<PreSignedUrlResponse>

    @GET("/photo/list")
    suspend fun fetchPhotos(
        @Query(value = "accountId") accountId: String,
        @Query(value = "identityToken") identityToken: String
    ): Response<List<Photo>>

    @POST("/account/answers")
    suspend fun saveAnswers(@Body saveAnswersRequest: SaveAnswersRequest): Response<EmptyJsonResponse>

    @GET("/question/list")
    suspend fun fetchQuestions(
        @Query(value = "accountId") accountId: String,
        @Query(value = "identityToken") identityToken: String
    ): Response<List<QuestionResponse>>

    @GET("/question/random")
    suspend fun fetchRandomQuestion(
        @Query(value = "questionIds") questionIds: List<Int>
    ): Response<QuestionResponse>

    @POST("/account/answers")
    suspend fun postAnswers(@Body postAnswersRequest: PostAnswersRequest): Response<EmptyJsonResponse>

    @GET("account/recommend")
    suspend fun fetchCards(
        @Query(value = "accountId") accountId: String,
        @Query(value = "identityToken") identityToken: String,
        @Query(value = "minAge") minAge: Int,
        @Query(value = "maxAge") maxAge: Int,
        @Query(value = "gender") gender: Boolean,
        @Query(value = "distance") distance: Int,
        @Query(value = "latitude") latitude: Double?,
        @Query(value = "longitude") longitude: Double?,
        @Query(value = "locationUpdatedAt") locationUpdatedAt: String?,
        @Query(value = "reset") reset: Boolean
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
        @Query(value = "identityToken") identityToken: String,
        @Query(value = "fetchedAt") fetchedAt: String
    ): Response<MutableList<Match>>

    @GET("swipe/clicked/list")
    suspend fun fetchClickedList(
        @Query(value = "accountId") accountId: String,
        @Query(value = "identityToken") identityToken: String,
        @Query(value = "fetchedAt") fetchedAt: String
    ): Response<MutableList<Clicked>>

    @POST("account/location")
    suspend fun postLocation(
        @Body locationRequest: LocationRequest
    ): Response<EmptyJsonResponse>

    companion object {
        operator fun invoke(
            okHttpClient: OkHttpClient
        ): BalanceAPI {

            val gson = GsonBuilder().registerTypeAdapter(
                OffsetDateTime::class.java,
                StringToOffsetDateTimeDeserializer()
            ).create()

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://nw9pdhgsp6.execute-api.ap-northeast-2.amazonaws.com/prod/balance/")
//                .baseUrl("http://10.0.2.2:8080/")
//                .baseUrl("http://localhost:8080/")
//                .baseUrl("http://192.168.1.100:8081/")
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(BalanceAPI::class.java)
        }
    }
}