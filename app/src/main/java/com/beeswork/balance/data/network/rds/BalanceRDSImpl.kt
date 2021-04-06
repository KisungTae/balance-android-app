package com.beeswork.balance.data.network.rds

import com.beeswork.balance.data.database.entity.Clicker
import com.beeswork.balance.data.database.entity.Match
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

    override suspend fun fetchChatMessages(
        accountId: String,
        identityToken: String,
        chatId: Long,
        recipientId: String,
        lastChatMessageId: Long
    ): Resource<List<ChatMessageResponse>> {
        return getResult {
            balanceAPI.fetchChatMessages(
                accountId,
                identityToken,
                chatId,
                recipientId,
                lastChatMessageId
            )
        }
    }

    override suspend fun reorderPhotos(
        accountId: String,
        identityToken: String,
        photoOrders: Map<String, Int>
    ): Resource<EmptyResponse> {
        return getResult {
            balanceAPI.reorderPhotos(
                ReorderPhotosBody(
                    accountId,
                    identityToken,
                    photoOrders
                )
            )
        }
    }

    override suspend fun deletePhoto(
        accountId: String,
        identityToken: String,
        photoKey: String
    ): Resource<EmptyResponse> {
        return getResult {
            balanceAPI.deletePhoto(
                DeletePhotoBody(
                    accountId,
                    identityToken,
                    photoKey
                )
            )
        }
    }

    override suspend fun uploadPhotoToS3(
        url: String,
        formData: Map<String, RequestBody>,
        multipartBody: MultipartBody.Part
    ): Resource<EmptyResponse> {
        return getResult { balanceAPI.uploadPhotoToS3(url, formData, multipartBody) }
    }

    override suspend fun addPhoto(
        accountId: String,
        identityToken: String,
        photoKey: String,
        sequence: Int
    ): Resource<PreSignedUrlResponse> {
        return getResult {
            balanceAPI.addPhoto(
                AddPhotoBody(
                    accountId,
                    identityToken,
                    photoKey,
                    sequence
                )
            )
        }
    }

    override suspend fun fetchPhotos(
        accountId: String,
        identityToken: String
    ): Resource<List<Photo>> {
        return getResult { balanceAPI.fetchPhotos(accountId, identityToken) }
    }

    override suspend fun saveAnswers(
        accountId: String,
        identityToken: String,
        answers: Map<Int, Boolean>
    ): Resource<EmptyResponse> {
        return getResult {
            balanceAPI.saveAnswers(
                SaveAnswersBody(
                    accountId,
                    identityToken,
                    answers
                )
            )
        }
    }

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

    override suspend fun postAnswers(
        accountId: String,
        identityToken: String,
        answers: Map<Int, Boolean>
    ): Resource<EmptyResponse> {
        return getResult {
            balanceAPI.postAnswers(
                PostAnswersBody(
                    accountId,
                    identityToken,
                    answers
                )
            )
        }
    }


    override suspend fun fetchCards(
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
    ): Resource<MutableList<CardResponse>> {

        return getResult {
            balanceAPI.fetchCards(
                accountId,
                identityToken,
                minAge,
                maxAge,
                gender,
                distance,
                latitude,
                longitude,
                locationUpdatedAt,
                reset
            )
        }
    }

    override suspend fun swipe(
        accountId: String,
        identityToken: String,
        swipeId: Long?,
        swipedId: String
    ): Resource<BalanceGameResponse> {
        return getResult {
            balanceAPI.swipe(SwipeBody(accountId, identityToken, swipeId, swipedId))
        }
    }

    override suspend fun click(
        accountId: String,
        identityToken: String,
        swipedId: String,
        swipeId: Long,
        answers: Map<Int, Boolean>
    ): Resource<ClickResponse> {
        return getResult {
            balanceAPI.click(ClickBody(accountId, identityToken, swipedId, swipeId, answers))
        }
    }

    override suspend fun postFCMToken(
        accountId: String,
        identityToken: String,
        token: String
    ): Resource<EmptyResponse> {
        return getResult {
            balanceAPI.postFCMToken(FCMTokenBody(accountId, identityToken, token))
        }
    }

    override suspend fun fetchMatches(
        accountId: String,
        identityToken: String,
        lastAccountUpdatedAt: String,
        lastMatchUpdatedAt: String,
        lastChatMessageCreatedAt: String
    ): Resource<MutableList<Match>> {
        return getResult {
            balanceAPI.fetchMatches(
                accountId,
                identityToken,
                lastAccountUpdatedAt,
                lastMatchUpdatedAt,
                lastChatMessageCreatedAt
            )
        }
    }

    override suspend fun fetchClickedList(
        accountId: String,
        identityToken: String,
        fetchedAt: String
    ): Resource<MutableList<Clicker>> {
        return getResult {
            balanceAPI.fetchClickedList(accountId, identityToken, fetchedAt)
        }
    }

    override suspend fun postLocation(
        accountId: String,
        identityToken: String,
        latitude: Double,
        longitude: Double,
        updatedAt: String
    ): Resource<EmptyResponse> {
        return getResult {
            balanceAPI.postLocation(
                LocationBody(
                    accountId,
                    identityToken,
                    80.0,
                    "longitude",
                    updatedAt
                )
            )
        }
    }

}











