package com.beeswork.balance.data.network.api

import com.beeswork.balance.data.network.converter.EnumConverterFactory
import com.beeswork.balance.data.network.request.chat.FetchedChatMessageBody
import com.beeswork.balance.data.network.request.chat.ReceivedChatMessageBody
import com.beeswork.balance.data.network.request.chat.SyncChatMessagesBody
import com.beeswork.balance.data.network.request.click.ClickBody
import com.beeswork.balance.data.network.request.common.ReportBody
import com.beeswork.balance.data.network.request.login.RefreshAccessTokenBody
import com.beeswork.balance.data.network.request.login.SocialLoginBody
import com.beeswork.balance.data.network.request.match.UnmatchBody
import com.beeswork.balance.data.network.request.profile.*
import com.beeswork.balance.data.network.request.setting.SaveFCMTokenBody
import com.beeswork.balance.data.network.request.setting.SaveLocationBody
import com.beeswork.balance.data.network.request.setting.SavePushSettingsBody
import com.beeswork.balance.data.network.request.swipe.SwipeBody
import com.beeswork.balance.data.network.response.chat.ListChatMessagesDTO
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.match.ListMatchesDTO
import com.beeswork.balance.data.network.response.click.ClickDTO
import com.beeswork.balance.data.network.response.click.CountClicksDTO
import com.beeswork.balance.data.network.response.login.LoginDTO
import com.beeswork.balance.data.network.response.login.RefreshAccessTokenDTO
import com.beeswork.balance.data.network.response.match.MatchDTO
import com.beeswork.balance.data.network.response.photo.PhotoDTO
import com.beeswork.balance.data.network.response.photo.PreSignedURLDTO
import com.beeswork.balance.data.network.response.profile.ProfileDTO
import com.beeswork.balance.data.network.response.profile.QuestionDTO
import com.beeswork.balance.data.network.response.setting.PushSettingDTO
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

    @POST("chat/message/fetched")
    suspend fun fetchedChatMessage(@Body fetchedChatMessageBody: FetchedChatMessageBody): Response<EmptyResponse>

    @POST("chat/message/received")
    suspend fun receivedChatMessage(@Body receivedChatMessageBody: ReceivedChatMessageBody): Response<EmptyResponse>

    @GET("chat/message/list")
    suspend fun listChatMessages(): Response<ListChatMessagesDTO>

    @POST("chat/message/sync")
    suspend fun syncChatMessages(@Body syncChatMessagesBody: SyncChatMessagesBody)


    @GET("click/list")
    suspend fun listClicks(
        @Query(value = "loadSize") loadSize: Int,
        @Query(value = "startPosition") startPosition: Int
    ): Response<List<ClickDTO>>

    @GET("click/fetch")
    suspend fun fetchClicks(
        @Query(value = "loadSize") loadSize: Int,
        @Query(value = "lastSwiperId") lastSwiperId: UUID?
    ): Response<List<ClickDTO>>

    @GET("/click/count")
    suspend fun countClicks(): Response<CountClicksDTO>


    @POST("login/social")
    @Headers("${HttpHeader.NO_AUTHENTICATION}: ${true}")
    suspend fun socialLogin(@Body socialLoginBody: SocialLoginBody): Response<LoginDTO>

    @POST("login/refresh-token")
    @Headers("${HttpHeader.NO_AUTHENTICATION}: ${true}")
    suspend fun loginWithRefreshToken(@Body refreshAccessTokenBody: RefreshAccessTokenBody): Response<LoginDTO>

    @POST("login/access-token/refresh")
    @Headers("${HttpHeader.NO_AUTHENTICATION}: ${true}")
    suspend fun refreshAccessToken(@Body refreshAccessTokenBody: RefreshAccessTokenBody): Response<RefreshAccessTokenDTO>


    @POST("click")
    suspend fun click(@Body clickBody: ClickBody): Response<MatchDTO>

    @POST("match/unmatch")
    suspend fun unmatch(@Body unmatchBody: UnmatchBody): Response<EmptyResponse>

    @GET("match/list")
    suspend fun listMatches(@Query(value = "fetchedAt") fetchedAt: OffsetDateTime): Response<ListMatchesDTO>


    @GET("photo/list")
    suspend fun fetchPhotos(): Response<List<PhotoDTO>>

    @POST("photo/reorder")
    suspend fun orderPhotos(@Body orderPhotosBody: OrderPhotosBody): Response<EmptyResponse>

    @POST("photo/delete")
    suspend fun deletePhoto(@Body deletePhotoBody: DeletePhotoBody): Response<EmptyResponse>

    @POST
    @Multipart
    suspend fun uploadPhotoToS3(
        @Url url: String,
        @PartMap formData: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part multipartBody: MultipartBody.Part
    ): Response<EmptyResponse>

    @POST("photo/save")
    suspend fun savePhoto(@Body savePhotoBody: SavePhotoBody): Response<EmptyResponse>

    @GET("photo/sign")
    suspend fun getPreSignedURL(@Query(value = "photoKey") photoKey: String): Response<PreSignedURLDTO>


    @POST("profile/email")
    suspend fun saveEmail(@Body saveEmailBody: SaveEmailBody): Response<EmptyResponse>

    @GET("profile/email")
    suspend fun getEmail(): Response<String>

    @GET("profile")
    suspend fun fetchProfile(): Response<ProfileDTO>

    @POST("/account/answers")
    suspend fun saveAnswers(@Body saveAnswersBody: SaveAnswersBody): Response<EmptyResponse>

    @GET("account/question/list")
    suspend fun listQuestions(): Response<List<QuestionDTO>>

    @POST("profile/about")
    suspend fun postAbout(@Body saveAboutBody: SaveAboutBody): Response<EmptyResponse>


    @POST("report/profile")
    suspend fun reportProfile(@Body reportBody: ReportBody): Response<EmptyResponse>

    @POST("report/match")
    suspend fun reportMatch(@Body reportBody: ReportBody): Response<EmptyResponse>


    @GET("setting/push")
    suspend fun getPushSetting(): Response<PushSettingDTO>

    @POST("account/delete")
    suspend fun deleteAccount(): Response<EmptyResponse>

    @POST("setting/push")
    suspend fun savePushSettings(@Body savePushSettingsBody: SavePushSettingsBody): Response<EmptyResponse>

    @POST("push-token/fcm")
    suspend fun saveFCMToken(@Body saveFcmTokenBody: SaveFCMTokenBody): Response<EmptyResponse>

    @POST("profile/location")
    suspend fun saveLocation(@Body saveLocationBody: SaveLocationBody): Response<EmptyResponse>


    @POST("swipe")
    suspend fun swipe(@Body swipeBody: SwipeBody): Response<List<QuestionDTO>>

    @GET("profile/recommend")
    suspend fun recommend(
        @Query(value = "minAge") minAge: Int,
        @Query(value = "maxAge") maxAge: Int,
        @Query(value = "gender") gender: Boolean,
        @Query(value = "distance") distance: Int,
        @Query(value = "pageIndex") pageIndex: Int
    ): Response<FetchCardsDTO>


    @GET("/question/random/list")
    suspend fun fetchRandomQuestions(): Response<List<QuestionDTO>>

    @GET("/question/random")
    suspend fun fetchRandomQuestion(@Query(value = "questionIds") questionIds: List<Int>): Response<QuestionDTO>




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
