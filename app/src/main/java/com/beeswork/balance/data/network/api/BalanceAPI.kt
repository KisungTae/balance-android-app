package com.beeswork.balance.data.network.api

import com.beeswork.balance.data.database.entity.Photo
import com.beeswork.balance.data.network.converter.EnumConverterFactory
import com.beeswork.balance.data.network.request.*
import com.beeswork.balance.data.network.response.*
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.match.ListMatchesDTO
import com.beeswork.balance.data.network.response.click.ClickDTO
import com.beeswork.balance.data.network.response.match.MatchDTO
import com.beeswork.balance.data.network.response.photo.PhotoDTO
import com.beeswork.balance.data.network.response.photo.PreSignedURLDTO
import com.beeswork.balance.data.network.response.profile.QuestionDTO
import com.beeswork.balance.data.network.response.swipe.FetchCardsDTO
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

    @GET("photo/sign")
    suspend fun getPreSignedURL(
        @Query(value = "accountId") accountId: UUID?,
        @Query(value = "identityToken") identityToken: UUID?,
        @Query(value = "photoKey") photoKey: UUID
    ): Response<PreSignedURLDTO>

    @GET("photo/list")
    suspend fun listPhotos(
        @Query(value = "accountId") accountId: UUID?,
        @Query(value = "identityToken") identityToken: UUID?
    ): Response<List<PhotoDTO>>

    @POST("account/question/answers")
    suspend fun saveAnswers(@Body saveAnswersBody: SaveAnswersBody): Response<EmptyResponse>

    @GET("account/question/list")
    suspend fun listQuestions(
        @Query(value = "accountId") accountId: UUID?,
        @Query(value = "identityToken") identityToken: UUID?
    ): Response<List<QuestionDTO>>

    @POST("profile/about")
    suspend fun postAbout(@Body saveAboutBody: SaveAboutBody): Response<EmptyResponse>

    @POST("setting")
    suspend fun postSettings(@Body postSettingsBody: PostSettingsBody): Response<EmptyResponse>

    @POST("swipe")
    suspend fun swipe(@Body swipeBody: SwipeBody): Response<List<QuestionDTO>>

    @POST("click")
    suspend fun click(@Body clickBody: ClickBody): Response<MatchDTO>

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

    @GET("profile/recommend")
    suspend fun recommend(
        @Query(value = "accountId") accountId: UUID?,
        @Query(value = "identityToken") identityToken: UUID?,
        @Query(value = "minAge") minAge: Int,
        @Query(value = "maxAge") maxAge: Int,
        @Query(value = "gender") gender: Boolean,
        @Query(value = "distance") distance: Int,
        @Query(value = "pageIndex") pageIndex: Int
    ): Response<FetchCardsDTO>

    @POST("profile/location")
    suspend fun postLocation(
        @Body postLocationBody: PostLocationBody
    ): Response<EmptyResponse>

    @POST("push-token/fcm")
    suspend fun postFCMToken(@Body postFcmTokenBody: PostFCMTokenBody): Response<EmptyResponse>


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

    @POST("photo/save")
    suspend fun savePhoto(
        @Body savePhotoBody: SavePhotoBody
    ): Response<EmptyResponse>

    @GET("/photo/list")
    suspend fun fetchPhotos(
        @Query(value = "accountId") accountId: String,
        @Query(value = "identityToken") identityToken: String
    ): Response<List<Photo>>


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




    companion object {
        operator fun invoke(
            okHttpClient: OkHttpClient
        ): BalanceAPI {

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(EndPoint.ACCOUNT_SERVICE_ENDPOINT)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create(GsonProvider.gson))
                .addConverterFactory(EnumConverterFactory())
                .build()
                .create(BalanceAPI::class.java)
        }
    }
}
