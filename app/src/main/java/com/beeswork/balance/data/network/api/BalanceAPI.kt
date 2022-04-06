package com.beeswork.balance.data.network.api

import com.beeswork.balance.data.network.converter.EnumConverterFactory
import com.beeswork.balance.data.network.request.chat.SyncChatMessagesBody
import com.beeswork.balance.data.network.request.click.ClickBody
import com.beeswork.balance.data.network.request.common.ReportBody
import com.beeswork.balance.data.network.request.login.LoginWithRefreshTokenBody
import com.beeswork.balance.data.network.request.login.RefreshAccessTokenBody
import com.beeswork.balance.data.network.request.login.SocialLoginBody
import com.beeswork.balance.data.network.request.match.SyncMatchBody
import com.beeswork.balance.data.network.request.match.UnmatchBody
import com.beeswork.balance.data.network.request.profile.*
import com.beeswork.balance.data.network.request.setting.SaveLocationBody
import com.beeswork.balance.data.network.request.setting.SavePushSettingsBody
import com.beeswork.balance.data.network.request.swipe.LikeBody
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.match.ListMatchesDTO
import com.beeswork.balance.data.network.response.login.LoginDTO
import com.beeswork.balance.data.network.response.login.RefreshAccessTokenDTO
import com.beeswork.balance.data.network.response.photo.PhotoDTO
import com.beeswork.balance.data.network.response.photo.PreSignedURLDTO
import com.beeswork.balance.data.network.response.profile.ProfileDTO
import com.beeswork.balance.data.network.response.profile.QuestionDTO
import com.beeswork.balance.data.network.response.setting.PushSettingDTO
import com.beeswork.balance.data.network.response.card.FetchCardsDTO
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.data.network.response.match.ClickDTO
import com.beeswork.balance.data.network.response.match.UnmatchDTO
import com.beeswork.balance.data.network.response.profile.FetchQuestionsDTO
import com.beeswork.balance.data.network.response.swipe.ListSwipesDTO
import com.beeswork.balance.internal.constant.EndPoint
import com.beeswork.balance.internal.constant.MatchPageFilter
import com.beeswork.balance.internal.provider.gson.GsonProvider
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.*

interface BalanceAPI {

    @POST("profile")
    suspend fun saveProfile(@Body profileDTO: ProfileDTO): Response<EmptyResponse>

    @POST("match/sync")
    suspend fun syncMatch(@Body syncMatchBody: SyncMatchBody): Response<EmptyResponse>

    @GET("chat/message/fetch")
    suspend fun fetchChatMessages(
        @Query(value = "chatId") chatId: UUID,
        @Query(value = "lastChatMessageId") lastChatMessageId: Long?,
        @Query(value = "loadSize") loadSize: Int
    ): Response<List<ChatMessageDTO>>

    @GET("chat/message/list")
    suspend fun listChatMessages(
        @Query(value = "chatId") chatId: UUID,
        @Query(value = "appToken") appToken: UUID,
        @Query(value = "startPosition") startPosition: Int,
        @Query(value = "loadSize") loadSize: Int
    ): Response<List<ChatMessageDTO>>

    @POST("chat/message/sync")
    suspend fun syncChatMessages(@Body syncChatMessagesBody: SyncChatMessagesBody)



    @GET("swipe/fetch")
    suspend fun fetchSwipes(
        @Query(value = "loadSize") loadSize: Int,
        @Query(value = "lastSwipeId") lastSwiperId: Long?
    ): Response<ListSwipesDTO>

    @GET("swipe/list")
    suspend fun listSwipes(
        @Query(value = "loadSize") loadSize: Int,
        @Query(value = "startPosition") startPosition: Int
    ): Response<ListSwipesDTO>




    @GET("match/fetch")
    suspend fun fetchMatches(
        @Query(value = "loadSize") loadSize: Int,
        @Query(value = "lastMatchId") lastMatchId: Long?,
        @Query(value = "matchPageFilter") matchPageFilter: MatchPageFilter?
    ): Response<ListMatchesDTO>

    @GET("match/list")
    suspend fun listMatches(
        @Query(value = "loadSize") loadSize: Int,
        @Query(value = "startPosition") startPosition: Int,
        @Query(value = "matchPageFilter") matchPageFilter: MatchPageFilter?
    ): Response<ListMatchesDTO>



    @POST("click")
    suspend fun click(@Body clickBody: ClickBody): Response<ClickDTO>

    @POST("match/unmatch")
    suspend fun unmatch(@Body unmatchBody: UnmatchBody): Response<UnmatchDTO>




    @POST("login/social")
    @Headers("${HttpHeader.NO_AUTHENTICATION}: ${true}")
    suspend fun socialLogin(@Body socialLoginBody: SocialLoginBody): Response<LoginDTO>

    @POST("login/refresh-token")
    @Headers("${HttpHeader.NO_AUTHENTICATION}: ${true}")
    suspend fun loginWithRefreshToken(@Body loginWithRefreshTokenBody: LoginWithRefreshTokenBody): Response<LoginDTO>

    @POST("login/access-token/refresh")
    @Headers("${HttpHeader.NO_AUTHENTICATION}: ${true}")
    suspend fun refreshAccessToken(@Body refreshAccessTokenBody: RefreshAccessTokenBody): Response<RefreshAccessTokenDTO>





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

    @POST("/question/answers")
    suspend fun saveAnswers(@Body saveAnswersBody: SaveAnswersBody): Response<EmptyResponse>

    @GET("question/list")
    suspend fun fetchQuestions(): Response<FetchQuestionsDTO>

    @POST("profile/bio")
    suspend fun saveBio(@Body saveBioBody: SaveBioBody): Response<EmptyResponse>


    @POST("report/profile")
    suspend fun reportProfile(@Body reportBody: ReportBody): Response<EmptyResponse>

    @POST("match/report")
    suspend fun reportMatch(@Body reportBody: ReportBody): Response<UnmatchDTO>


    @GET("setting/push")
    suspend fun getPushSetting(): Response<PushSettingDTO>

    @POST("account/delete")
    suspend fun deleteAccount(): Response<EmptyResponse>

    @POST("setting/push")
    suspend fun savePushSettings(@Body savePushSettingsBody: SavePushSettingsBody): Response<EmptyResponse>


    @POST("profile/location")
    suspend fun saveLocation(@Body saveLocationBody: SaveLocationBody): Response<EmptyResponse>


    @POST("like")
    suspend fun like(@Body likeBody: LikeBody): Response<List<QuestionDTO>>

    @GET("profile/recommend")
    suspend fun recommend(
        @Query(value = "minAge") minAge: Int,
        @Query(value = "maxAge") maxAge: Int,
        @Query(value = "gender") gender: Boolean,
        @Query(value = "distance") distance: Int,
        @Query(value = "pageIndex") pageIndex: Int
    ): Response<FetchCardsDTO>

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
