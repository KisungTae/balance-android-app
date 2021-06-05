package com.beeswork.balance.data.network.rds

import com.beeswork.balance.data.database.entity.*
import com.beeswork.balance.data.network.response.*
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface BalanceRDS {

    suspend fun reorderPhotos(
        accountId: String,
        identityToken: String,
        photoOrders: Map<String, Int>
    ): Resource<EmptyResponse>

    suspend fun uploadPhotoToS3(
        url: String,
        formData: Map<String, RequestBody>,
        multipartBody: MultipartBody.Part
    ): Resource<EmptyResponse>

    suspend fun fetchPhotos(
        accountId: String,
        identityToken: String,
    ): Resource<List<Photo>>

//    suspend fun saveAnswers(
//        accountId: String,
//        identityToken: String,
//        answers: Map<Int, Boolean>
//    ): Resource<EmptyResponse>

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
    ): Resource<EmptyResponse>

//    suspend fun swipe(
//        accountId: String,
//        identityToken: String,
//        swipeId: Long?,
//        swipedId: String
//    ): Resource<BalanceGameResponse>

//    suspend fun click(
//        accountId: String,
//        identityToken: String,
//        swipedId: String,
//        swipeId: Long,
//        answers: Map<Int, Boolean>
//    ): Resource<ClickResponse>


}