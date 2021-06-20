package com.beeswork.balance.data.network.rds

import com.beeswork.balance.data.database.entity.Photo
import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.request.*
import com.beeswork.balance.data.network.response.*
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class BalanceRDSImpl(
    private val balanceAPI: BalanceAPI
) : BaseRDS(), BalanceRDS {





    override suspend fun uploadPhotoToS3(
        url: String,
        formData: Map<String, RequestBody>,
        multipartBody: MultipartBody.Part
    ): Resource<EmptyResponse> {
        return getResult { balanceAPI.uploadPhotoToS3(url, formData, multipartBody) }
    }

    override suspend fun fetchPhotos(
        accountId: String,
        identityToken: String
    ): Resource<List<Photo>> {
        return getResult { balanceAPI.fetchPhotos(accountId, identityToken) }
    }

//    override suspend fun saveAnswers(
//        accountId: String,
//        identityToken: String,
//        answers: Map<Int, Boolean>
//    ): Resource<EmptyResponse> {
//        return getResult {
//            balanceAPI.saveAnswers(
//                SaveAnswersBody(
//                    accountId,
//                    identityToken,
//                    answers
//                )
//            )
//        }
//    }

    override suspend fun fetchQuestions(
        accountId: String,
        identityToken: String
    ): Resource<List<QuestionResponse>> {
        return getResult { balanceAPI.fetchQuestions(accountId, identityToken) }
    }

    override suspend fun fetchRandomQuestion(
        questionIds: List<Int>
    ): Resource<QuestionResponse> {
        return getResult { balanceAPI.fetchRandomQuestion(questionIds) }
    }

//    override suspend fun postAnswers(
//        accountId: String,
//        identityToken: String,
//        answers: Map<Int, Boolean>
//    ): Resource<EmptyResponse> {
//        return getResult {
//            balanceAPI.postAnswers(
//                PostAnswersBody(
//                    accountId,
//                    identityToken,
//                    answers
//                )
//            )
//        }
//    }


//    override suspend fun fetchCards(
//        accountId: String,
//        identityToken: String,
//        minAge: Int,
//        maxAge: Int,
//        gender: Boolean,
//        distance: Int,
//        latitude: Double?,
//        longitude: Double?,
//        locationUpdatedAt: String?,
//        reset: Boolean
//    ): Resource<MutableList<CardDTO>> {
//
//        return getResult {
//            balanceAPI.fetchCards(
//                accountId,
//                identityToken,
//                minAge,
//                maxAge,
//                gender,
//                distance,
//                latitude,
//                longitude,
//                locationUpdatedAt,
//                reset
//            )
//        }
//    }

//    override suspend fun swipe(
//        accountId: String,
//        identityToken: String,
//        swipeId: Long?,
//        swipedId: String
//    ): Resource<BalanceGameResponse> {
//        return getResult {
//            balanceAPI.swipe(SwipeBody(accountId, identityToken, swipeId, swipedId))
//        }
//    }

//    override suspend fun click(
//        accountId: String,
//        identityToken: String,
//        swipedId: String,
//        swipeId: Long,
//        answers: Map<Int, Boolean>
//    ): Resource<ClickResponse> {
//        return getResult {
//            balanceAPI.click(ClickBody(accountId, identityToken, swipedId, swipeId, answers))
//        }
//    }


}











