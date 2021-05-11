package com.beeswork.balance.data.network.api

import com.beeswork.balance.data.database.entity.Click
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.database.entity.Photo
import com.beeswork.balance.data.network.request.*
import com.beeswork.balance.data.network.response.*
import com.beeswork.balance.data.network.response.chat.ListChatMessagesDTO
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.match.ListMatchesDTO
import com.beeswork.balance.data.network.response.swipe.ClickDTO
import com.beeswork.balance.internal.constant.EndPoint
import com.beeswork.balance.internal.provider.gson.GsonProvider
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.threeten.bp.OffsetDateTime
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.*

interface BalanceAPI {

    @GET("click/list")
    suspend fun listClicks(
        @Query(value = "accountId") accountId: UUID?,
        @Query(value = "identityToken") identityToken: UUID?,
        @Query(value = "fetchedAt") fetchedAt: OffsetDateTime
    ): Response<List<ClickDTO>>

    @POST("match/unmatch")
    suspend fun unmatch(
        @Body unmatchBody: UnmatchBody
    ): Response<EmptyResponse>

    @POST("report/profile")
    suspend fun reportProfile(
        @Body reportBody: ReportBody
    ): Response<EmptyResponse>

    @POST("report/match")
    suspend fun reportMatch(
        @Body reportBody: ReportBody
    ): Response<EmptyResponse>

    @POST("chat/message/sync")
    suspend fun syncChatMessages(
        @Body syncChatMessagesBody: SyncChatMessagesBody
    )

    @GET("match/list")
    suspend fun listMatches(
        @Query(value = "accountId") accountId: UUID?,
        @Query(value = "identityToken") identityToken: UUID?,
        @Query(value = "fetchedAt") fetchedAt: OffsetDateTime
    ): Response<ListMatchesDTO>


    @GET("chat/message/list")
    suspend fun fetchChatMessages(
        @Query(value = "accountId") accountId: String,
        @Query(value = "identityToken") identityToken: String,
        @Query(value = "chatId") chatId: Long,
        @Query(value = "recipientId") recipientId: String,
        @Query(value = "lastChatMessageId") lastChatMessageId: Long
    ): Response<List<ChatMessageResponse>>

    @POST("photo/reorder")
    suspend fun reorderPhotos(
        @Body reorderPhotosBody: ReorderPhotosBody
    ): Response<EmptyResponse>

    @POST("photo/delete")
    suspend fun deletePhoto(
        @Body deletePhotoBody: DeletePhotoBody
    ): Response<EmptyResponse>

    @POST
    @Multipart
    suspend fun uploadPhotoToS3(
        @Url url: String,
        @PartMap formData: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part multipartBody: MultipartBody.Part
    ): Response<EmptyResponse>

    @POST("photo/add")
    suspend fun addPhoto(
        @Body addPhotoBody: AddPhotoBody
    ): Response<PreSignedUrlResponse>

    @GET("/photo/list")
    suspend fun fetchPhotos(
        @Query(value = "accountId") accountId: String,
        @Query(value = "identityToken") identityToken: String
    ): Response<List<Photo>>

    @POST("/account/answers")
    suspend fun saveAnswers(@Body saveAnswersBody: SaveAnswersBody): Response<EmptyResponse>

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
    suspend fun postAnswers(@Body postAnswersBody: PostAnswersBody): Response<EmptyResponse>

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
    suspend fun swipe(@Body swipeBody: SwipeBody): Response<BalanceGameResponse>

    @POST("swipe/click")
    suspend fun click(@Body clickBody: ClickBody): Response<ClickResponse>

    @POST("push-token/fcm")
    suspend fun postFCMToken(@Body postFcmTokenBody: PostFCMTokenBody): Response<EmptyResponse>

    @GET("match/list")
    suspend fun fetchMatches(
        @Query(value = "accountId") accountId: String,
        @Query(value = "identityToken") identityToken: String,
        @Query(value = "lastAccountUpdatedAt") lastAccountUpdatedAt: String,
        @Query(value = "lastMatchUpdatedAt") lastMatchUpdatedAt: String,
        @Query(value = "lastChatMessageCreatedAt") lastChatMessageCreatedAt: String
    ): Response<MutableList<Match>>

    @GET("swipe/clicked/list")
    suspend fun fetchClickedList(
        @Query(value = "accountId") accountId: String,
        @Query(value = "identityToken") identityToken: String,
        @Query(value = "fetchedAt") fetchedAt: String
    ): Response<MutableList<Click>>

    @POST("profile/location")
    suspend fun postLocation(
        @Body postLocationBody: PostLocationBody
    ): Response<EmptyResponse>

    companion object {
        operator fun invoke(
            okHttpClient: OkHttpClient
        ): BalanceAPI {

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(EndPoint.ACCOUNT_SERVICE_ENDPOINT)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create(GsonProvider.gson))
                .build()
                .create(BalanceAPI::class.java)
        }
    }
}