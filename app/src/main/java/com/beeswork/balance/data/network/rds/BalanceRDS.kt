package com.beeswork.balance.data.network.rds

import com.beeswork.balance.data.database.entity.Click
import com.beeswork.balance.data.database.entity.Clicked
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.database.entity.Photo
import com.beeswork.balance.data.network.response.*
import com.beeswork.balance.internal.Resource
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.threeten.bp.OffsetDateTime
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface BalanceRDS {

    suspend fun reorderPhotos(
        accountId: String,
        identityToken: String,
        photoOrders: Map<String, Int>
    ): Resource<EmptyJsonResponse>

    suspend fun deletePhoto(
        accountId: String,
        identityToken: String,
        photoKey: String
    ): Resource<EmptyJsonResponse>

    suspend fun uploadPhotoToS3(
        url: String,
        formData: Map<String, RequestBody>,
        photo: MultipartBody.Part
    ): Resource<EmptyJsonResponse>

    suspend fun addPhoto(
        accountId: String,
        identityToken: String,
        photoKey: String,
        sequence: Int
    ): Resource<PreSignedUrlResponse>

    suspend fun fetchPhotos(
        accountId: String,
        identityToken: String,
    ): Resource<List<Photo>>

    suspend fun saveAnswers(
        accountId: String,
        identityToken: String,
        answers: Map<Int, Boolean>
    ): Resource<EmptyJsonResponse>

    suspend fun fetchQuestions(
        accountId: String,
        identityToken: String
    ): Resource<List<QuestionResponse>>


    suspend fun fetchRandomQuestion(
        questionIds: List<Int>
    ): Resource<QuestionResponse>

    suspend fun postAnswers(
        accountId: String,
        identityToken: String,
        answers: Map<Int, Boolean>
    ): Resource<EmptyJsonResponse>

    suspend fun fetchCards(
        accountId: String,
        identityToken: String,
        minAge: Int,
        maxAge: Int,
        gender: Boolean,
        distance: Int,
        latitude: Double?,
        longitude: Double?,
        locationUpdatedAt: String?,
        reset: Boolean
    ): Resource<MutableList<CardResponse>>

    suspend fun swipe(
        accountId: String,
        identityToken: String,
        swipeId: Long?,
        swipedId: String
    ): Resource<BalanceGameResponse>

    suspend fun click(
        accountId: String,
        identityToken: String,
        swipedId: String,
        swipeId: Long,
        answers: Map<Int, Boolean>
    ): Resource<ClickResponse>

    suspend fun postFCMToken(
        accountId: String,
        identityToken:String,
        token: String
    ): Resource<EmptyJsonResponse>

    suspend fun fetchMatches(
        accountId: String,
        identityToken: String,
        fetchedAt: String
    ): Resource<MutableList<Match>>

    suspend fun fetchClickedList(
        accountId: String,
        identityToken: String,
        fetchedAt: String
    ): Resource<MutableList<Clicked>>

    suspend fun postLocation(
        accountId: String,
        identityToken: String,
        latitude: Double,
        longitude: Double,
        updatedAt: String
    ): Resource<EmptyJsonResponse>
}