package com.beeswork.balance.data.database.repository

import android.content.Context
import android.webkit.MimeTypeMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.beeswork.balance.R
import com.beeswork.balance.data.database.dao.*
import com.beeswork.balance.data.database.entity.*
import com.beeswork.balance.data.network.rds.BalanceRDS
import com.beeswork.balance.data.network.response.*
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.swipe.CardDTO
import com.beeswork.balance.internal.constant.ExceptionCode
import id.zelory.compressor.Compressor
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.threeten.bp.OffsetDateTime
import java.io.File
import java.io.IOException


class BalanceRepositoryImpl(
    private val context: Context,
    private val matchDAO: MatchDAO,
    private val chatMessageDAO: ChatMessageDAO,
    private val swipeDAO: SwipeDAO,
    private val fcmTokenDAO: FCMTokenDAO,
    private val clickDAO: ClickDAO,
    private val profileDAO: ProfileDAO,
    private val photoDAO: PhotoDAO,
    private val locationDAO: LocationDAO,
    private val balanceRDS: BalanceRDS,
    private val preferenceProvider: PreferenceProvider
) : BalanceRepository {

//  ################################################################################# //
//  ##################################### SWIPE ##################################### //
//  ################################################################################# //

    private val mutableFetchClickedListResponse = MutableLiveData<Resource<List<Click>>>()
    override val fetchClickListResponse: LiveData<Resource<List<Click>>>
        get() = mutableFetchClickedListResponse

    override fun fetchClickedList() {

//        CoroutineScope(Dispatchers.IO).launch {
//
//            val clickedResource = balanceRDS.fetchClickedList(
//                preferenceProvider.getAccountId1(),
//                preferenceProvider.getIdentityToken1(),
//                preferenceProvider.getClickedFetchedAt()
//            )
//
//            if (clickedResource.status == Resource.Status.SUCCESS) {
//                val clickedList = clickedResource.data!!
//                if (clickedList.size > 0) {
//                    clickerDAO.insert(clickedList)
//                    clickedResource.data.sortByDescending { c -> c.updatedAt }
//                    preferenceProvider.putClickedFetchedAt(clickedList[0].updatedAt.toString())
//                    clickedList.clear()
//                }
//            }
//            clickerDAO.deleteIfMatched()
//            mutableFetchClickedListResponse.postValue(clickedResource)
//        }
    }





    private val mutableBalanceGameResponse = MutableLiveData<Resource<BalanceGameResponse>>()
    override val balanceGame: LiveData<Resource<BalanceGameResponse>>
        get() = mutableBalanceGameResponse

    override fun swipe(swipeId: Long?, swipedId: String) {

//        CoroutineScope(Dispatchers.IO).launch {
//            val accountId = preferenceProvider.getAccountId1()
//            val identityToken = preferenceProvider.getIdentityToken1()
//
//            mutableBalanceGameResponse.postValue(Resource.loading())
//            val questionResource = balanceRDS.swipe(accountId, identityToken, swipeId, swipedId)
//            mutableBalanceGameResponse.postValue(questionResource)
//        }
    }

    private val mutableClickResponse = MutableLiveData<Resource<ClickResponse>>()
    override val clickResponse: LiveData<Resource<ClickResponse>>
        get() = mutableClickResponse

    //  TEST 1. even if you leave the app before completing the network call, when you come back to the app
    //          you will get the response. The response is received in the background
    override fun click(swipedId: String, swipeId: Long, answers: Map<Int, Boolean>) {

//        CoroutineScope(Dispatchers.IO).launch {
//            val accountId = preferenceProvider.getAccountId1()
//            val identityToken = preferenceProvider.getIdentityToken1()
//
//            val clickResource =
//                balanceRDS.click(accountId, identityToken, swipedId, swipeId, answers)
//
//            if (clickResource.status == Resource.Status.SUCCESS) {
//                val data = clickResource.data!!
//                val result = data.result
//                val match = data.match
//
//                if (result == NotificationType.MATCH) {
//                    setNewMatch(match)
//                    matchDAO.insert(match)
//                    clickedDAO.insert(Clicked(match.matchedId))
//                    clickerDAO.deleteIfMatched()
//                } else if (result == NotificationType.CLICKED) {
////                    TODO: check if clicked is in match table if so not inserting
//                    clickedDAO.insert(Clicked(match.matchedId))
//                }
//            }
//            mutableClickResponse.postValue(clickResource)
//        }
    }


//  ################################################################################# //
//  ##################################### MATCH ##################################### //
//  ################################################################################# //


    private val mutableFetchMatchesResource = MutableLiveData<Resource<List<Match>>>()
    override val fetchMatchesResponse: LiveData<Resource<List<Match>>>
        get() = mutableFetchMatchesResource


    override fun fetchMatches() {

        CoroutineScope(Dispatchers.IO).launch {

//            var fetchedAt = preferenceProvider.getMatchFetchedAt1()
//            val matchResource = balanceRDS.fetchMatches(
//                preferenceProvider.getAccountId1(),
//                preferenceProvider.getIdentityToken1(),
//                fetchedAt,
//                fetchedAt,
//                fetchedAt
//            )

//            if (matchResource.status == Resource.Status.SUCCESS) {
//                val fetchedMatches = matchResource.data!!
//
//                for (i in (fetchedMatches.size - 1) downTo 0) {
//
//                    val fetchedMatch = fetchedMatches[i]
//
//                    if (fetchedMatch.updatedAt!! > fetchedAt)
//                        fetchedAt = fetchedMatch.updatedAt
//
//                    if (matchDAO.existsByChatId(fetchedMatch.chatId)) {
//                        matchDAO.update(
//                            fetchedMatch.chatId,
//                            fetchedMatch.photoKey,
//                            fetchedMatch.unmatched
//                        )
//                        fetchedMatches.removeAt(i)
//                    } else {
//                        setNewMatch(fetchedMatch)
//                    }
//                }
//
//                if (fetchedMatches.size > 0) {
//                    matchDAO.insert(fetchedMatches)
//                    fetchedMatches.clear()
//                }
//
//                clickedDAO.deleteIfMatched()
//                preferenceProvider.putMatchFetchedAt(fetchedAt)
//            }
//            mutableFetchMatchesResource.postValue(matchResource)
        }
    }

    private fun setNewMatch(match: Match) {
        val currentOffsetDateTime = OffsetDateTime.now()
//        match.unreadMessageCount = 1
//        match.recentMessage = context.getString(R.string.default_recent_message)
//        match.lastReadAt = currentOffsetDateTime
//        match.lastReceivedAt = currentOffsetDateTime
    }

//  ################################################################################# //
//  ##################################### ACCOUNT ################################### //
//  ################################################################################# //

    private val mutableCards = MutableLiveData<Resource<List<CardDTO>>>()
    override val cards: LiveData<Resource<List<CardDTO>>>
        get() = mutableCards

    private var fetchingCards = false
    private var fetchedNoCards = false

    override fun fetchCards(reset: Boolean) {

//        if (!fetchingCards && (reset || !fetchedNoCards)) {
//            fetchingCards = true
//
//            mutableCards.postValue(Resource.loading())
//
//            CoroutineScope(Dispatchers.IO).launch {
//                val accountId = preferenceProvider.getAccountId1()
//                val identityToken = preferenceProvider.getIdentityToken1()
//                val minAge = preferenceProvider.getMinAgeBirthYear()
//                val maxAge = preferenceProvider.getMaxAgeBirthYear()
//                val gender = preferenceProvider.getGender()
//                val distance = preferenceProvider.getDistanceInMeters()
//                var latitude: Double? = null
//                var longitude: Double? = null
//                var locationUpdatedAt: OffsetDateTime? = null
//
//                val location: Location? = locationDAO.get()
//                if (location != null && !location.synced) {
//                    latitude = location.latitude
//                    longitude = location.longitude
//                    locationUpdatedAt = location.updatedAt
//                }
//
//                val cardsResource =
//                    balanceRDS.fetchCards(
//                        accountId,
//                        identityToken,
//                        minAge,
//                        maxAge,
//                        gender,
//                        distance,
//                        latitude,
//                        longitude,
//                        locationUpdatedAt?.toString(),
//                        reset
//                    )
//
//                if (cardsResource.status == Resource.Status.SUCCESS) {
//
//                    val fetchedCards = cardsResource.data
//
//                    if (fetchedCards == null || fetchedCards.size == 0) {
//                        fetchedNoCards = true
//                    } else {
//
//                        fetchedNoCards = false
//
//                        val clickIds = clickedDAO.getClickedIds().toHashSet()
////                        clickIds.addAll(matchDAO.getMatchedIds())
//
//                        val endIndex = fetchedCards.size - 1
//
//                        for (i in endIndex downTo 0) {
//                            if (clickIds.contains(fetchedCards[i].accountId)) {
//                                fetchedCards.removeAt(i)
//                                continue
//                            }
//                            fetchedCards[i].birthYear =
//                                Convert.birthYearToAge(fetchedCards[i].birthYear)
//                        }
//                    }
//
//                    if (locationUpdatedAt != null)
//                        locationDAO.sync(locationUpdatedAt)
//                } else if (cardsResource.status == Resource.Status.ERROR) {
//                    fetchedNoCards = false
//                }
//
//                mutableCards.postValue(cardsResource)
//                fetchingCards = false
//            }
//        }
    }

    override suspend fun saveAnswers(answers: Map<Int, Boolean>): Resource<EmptyResponse> {
        return balanceRDS.saveAnswers(
            preferenceProvider.getAccountId().toString(),
            preferenceProvider.getIdentityToken().toString(),
            answers
        )
    }

    override suspend fun fetchQuestions(): Resource<List<QuestionResponse>> {
        return balanceRDS.fetchQuestions(
            preferenceProvider.getAccountId().toString(),
            preferenceProvider.getIdentityToken().toString()
        )
    }

    override suspend fun fetchRandomQuestion(questionIds: List<Int>): Resource<QuestionResponse> {
        return balanceRDS.fetchRandomQuestion(questionIds)
    }


//  ################################################################################# //
//  ##################################### PHOTO ##################################### //
//  ################################################################################# //

    override suspend fun fetchPhotos(): Resource<List<Photo>> {
        if (photoDAO.existsBySynced(false) || photoDAO.count() == 0) {
            val accountId = preferenceProvider.getAccountId().toString()
            val identityToken = preferenceProvider.getAccountId().toString()

            val response = balanceRDS.fetchPhotos(accountId, identityToken)
            if (response.status == Resource.Status.ERROR)
                return response

            val photos = response.data
            if (photos == null || photos.isEmpty())
                photoDAO.deletePhotosNotIn(listOf(""))
            else {
                photoDAO.deletePhotosNotIn(photos.map { it.key })
                photos.forEach { it.synced = true }
                photoDAO.insert(photos)
            }
        }
        return Resource.success(photoDAO.getPhotos())
    }


    override suspend fun uploadPhoto(
        photoKey: String,
        photoExtension: String,
        photoPath: String,
        photoSequence: Int
    ): Resource<EmptyResponse> {
        try {
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(photoExtension)?.let { mimeType ->
                val photo = File(photoPath)
                val compressedPhoto = Compressor.compress(context, photo)
                if (compressedPhoto.length() > Photo.MAX_SIZE)
                    return Resource.error(
                        context.resources.getString(R.string.photo_out_of_size_exception),
                        null,
                    )

                photoDAO.insert(Photo(photoKey, photoSequence, false))

                val fetchPreSignedUrlResponse = balanceRDS.addPhoto(
                    preferenceProvider.getAccountId().toString(),
                    preferenceProvider.getIdentityToken().toString(),
                    photoKey,
                    photoSequence
                )

                if (fetchPreSignedUrlResponse.isSuccess()) {
                    fetchPreSignedUrlResponse.data?.let {
                        photoDAO.sync(photoKey, true)

                        val formData = mutableMapOf<String, RequestBody>()
                        for ((key, value) in it.fields) {
                            formData[key] = RequestBody.create(MultipartBody.FORM, value)
                        }

                        val requestBody =
                            RequestBody.create(MediaType.parse(mimeType), compressedPhoto)

                        val multiPartBody =
                            MultipartBody.Part.createFormData("file", photoKey, requestBody)

                        val uploadPhotoToS3Response =
                            balanceRDS.uploadPhotoToS3(it.url, formData, multiPartBody)

                        if (uploadPhotoToS3Response.isSuccess()) {
                            deleteFile(compressedPhoto)
                            deleteFile(photo)
                        }
                        return uploadPhotoToS3Response
                    } ?: return Resource.error(
                        context.resources.getString(R.string.presigned_url_not_found_exception),
                        null
                    )
                }
                return Resource.error(
                    fetchPreSignedUrlResponse.errorMessage,
                    fetchPreSignedUrlResponse.error
                )
            } ?: return Resource.error(
                context.resources.getString(R.string.mime_type_not_found_exception),
                null
            )
        } catch (e: NoSuchFileException) {
            return Resource.error(
                context.resources.getString(R.string.photo_not_found_exception),
                null
            )
        }
    }

    private fun deleteFile(file: File) {
        try {
            file.delete()
        } catch (e: IOException) {
            // TODO: log exception?
        } catch (e: SecurityException) {
            // TODO: log exception?
        }
    }

    override suspend fun deletePhoto(photoKey: String): Resource<EmptyResponse> {
        photoDAO.sync(photoKey, false)
        val response = balanceRDS.deletePhoto(
            preferenceProvider.getAccountId().toString(),
            preferenceProvider.getIdentityToken().toString(),
            photoKey
        )
        if (response.isSuccess() || response.error == ExceptionCode.PHOTO_NOT_FOUND_EXCEPTION)
            photoDAO.deletePhoto(photoKey)
        return response
    }

    override suspend fun reorderPhoto(photoOrders: Map<String, Int>): Resource<EmptyResponse> {
        for ((k, v) in photoOrders) {
            photoDAO.sync(k, false)
        }

        val response = balanceRDS.reorderPhotos(
            preferenceProvider.getAccountId().toString(),
            preferenceProvider.getIdentityToken().toString(),
            photoOrders
        )

        for ((photoKey, sequence) in photoOrders) {
            photoDAO.sync(photoKey, true)
            if (response.isSuccess())
                photoDAO.updateSequence(photoKey, sequence)
        }
        return response
    }


//  ################################################################################# //
//  ##################################### MESSAGE ##################################### //
//  ################################################################################# //



}




