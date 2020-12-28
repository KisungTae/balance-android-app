package com.beeswork.balance.data.database.repository

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.beeswork.balance.R
import com.beeswork.balance.data.database.dao.*
import com.beeswork.balance.data.database.entity.*
import com.beeswork.balance.data.network.rds.BalanceRDS
import com.beeswork.balance.data.network.response.*
import com.beeswork.balance.internal.provider.PreferenceProvider
import com.beeswork.balance.internal.Resource
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.constant.NotificationType
import com.beeswork.balance.internal.converter.Convert
import id.zelory.compressor.Compressor
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.threeten.bp.OffsetDateTime
import java.io.File
import java.io.IOException
import kotlin.random.Random


class BalanceRepositoryImpl(
    private val context: Context,
    private val matchDAO: MatchDAO,
    private val messageDAO: MessageDAO,
    private val clickDAO: ClickDAO,
    private val fcmTokenDAO: FCMTokenDAO,
    private val clickedDAO: ClickedDAO,
    private val profileDAO: ProfileDAO,
    private val photoDAO: PhotoDAO,
    private val locationDAO: LocationDAO,
    private val balanceRDS: BalanceRDS,
    private val preferenceProvider: PreferenceProvider
) : BalanceRepository {

//  ################################################################################# //
//  ##################################### SWIPE ##################################### //
//  ################################################################################# //

    private val mutableFetchClickedListResponse = MutableLiveData<Resource<List<Clicked>>>()
    override val fetchClickedListResponse: LiveData<Resource<List<Clicked>>>
        get() = mutableFetchClickedListResponse

    override fun fetchClickedList() {

        CoroutineScope(Dispatchers.IO).launch {

            val clickedResource = balanceRDS.fetchClickedList(
                preferenceProvider.getAccountId(),
                preferenceProvider.getIdentityToken(),
                preferenceProvider.getClickedFetchedAt()
            )

            if (clickedResource.status == Resource.Status.SUCCESS) {
                val clickedList = clickedResource.data!!
                if (clickedList.size > 0) {
                    clickedDAO.insert(clickedList)
                    clickedResource.data.sortByDescending { c -> c.updatedAt }
                    preferenceProvider.putClickedFetchedAt(clickedList[0].updatedAt.toString())
                    clickedList.clear()
                }
            }
            clickedDAO.deleteIfMatched()
            mutableFetchClickedListResponse.postValue(clickedResource)
        }
    }

    override suspend fun getClickedCount(): LiveData<Int> {
        return withContext(Dispatchers.IO) {
            return@withContext clickedDAO.count()
        }
    }

    override suspend fun getClickedList(): DataSource.Factory<Int, Clicked> {
        return withContext(Dispatchers.IO) {
            return@withContext clickedDAO.getClickedList()
        }
    }

    private val mutableBalanceGameResponse = MutableLiveData<Resource<BalanceGameResponse>>()
    override val balanceGame: LiveData<Resource<BalanceGameResponse>>
        get() = mutableBalanceGameResponse

    override fun swipe(swipeId: Long?, swipedId: String) {

        CoroutineScope(Dispatchers.IO).launch {
            val accountId = preferenceProvider.getAccountId()
            val identityToken = preferenceProvider.getIdentityToken()

            mutableBalanceGameResponse.postValue(Resource.loading())
            val questionResource = balanceRDS.swipe(accountId, identityToken, swipeId, swipedId)
            mutableBalanceGameResponse.postValue(questionResource)
        }
    }

    private val mutableClickResponse = MutableLiveData<Resource<ClickResponse>>()
    override val clickResponse: LiveData<Resource<ClickResponse>>
        get() = mutableClickResponse

    //  TEST 1. even if you leave the app before completing the network call, when you come back to the app
    //          you will get the response. The response is received in the background
    override fun click(swipedId: String, swipeId: Long, answers: Map<Int, Boolean>) {

        CoroutineScope(Dispatchers.IO).launch {
            val accountId = preferenceProvider.getAccountId()
            val identityToken = preferenceProvider.getIdentityToken()

            val clickResource =
                balanceRDS.click(accountId, identityToken, swipedId, swipeId, answers)

            if (clickResource.status == Resource.Status.SUCCESS) {
                val data = clickResource.data!!
                val notificationType = data.notificationType
                val match = data.match

                if (notificationType == NotificationType.MATCH) {
                    setNewMatch(match)
                    matchDAO.insert(match)
                    clickedDAO.deleteIfMatched()
                } else if (notificationType == NotificationType.CLICKED) {
                    clickDAO.insert(Click(match.matchedId))
                }
            }
            mutableClickResponse.postValue(clickResource)
        }
    }


//  ################################################################################# //
//  ##################################### MATCH ##################################### //
//  ################################################################################# //


    private val mutableFetchMatchesResource = MutableLiveData<Resource<List<Match>>>()
    override val fetchMatchesResponse: LiveData<Resource<List<Match>>>
        get() = mutableFetchMatchesResource

    override suspend fun getMatches(): DataSource.Factory<Int, Match> {
        return withContext(Dispatchers.IO) {
            return@withContext matchDAO.getMatches()
        }
    }

    override suspend fun getUnreadMessageCount(): LiveData<Int> {
        return withContext(Dispatchers.IO) {
            return@withContext matchDAO.countUnreadMessageCount()
        }
    }


    override fun unmatch() {
        CoroutineScope(Dispatchers.IO).launch {
            matchDAO.unmatch("88277")
        }
    }

    override fun fetchMatches() {

        CoroutineScope(Dispatchers.IO).launch {

            var fetchedAt = preferenceProvider.getMatchFetchedAt()
            val matchResource = balanceRDS.fetchMatches(
                preferenceProvider.getAccountId(),
                preferenceProvider.getIdentityToken(),
                fetchedAt
            )

            if (matchResource.status == Resource.Status.SUCCESS) {
                val fetchedMatches = matchResource.data!!

                for (i in (fetchedMatches.size - 1) downTo 0) {

                    val fetchedMatch = fetchedMatches[i]

                    if (fetchedMatch.updatedAt!! > fetchedAt)
                        fetchedAt = fetchedMatch.updatedAt

                    if (matchDAO.existsByChatId(fetchedMatch.chatId)) {
                        matchDAO.update(
                            fetchedMatch.chatId,
                            fetchedMatch.photoKey,
                            fetchedMatch.unmatched
                        )
                        fetchedMatches.removeAt(i)
                    } else {
                        setNewMatch(fetchedMatch)
                    }
                }

                if (fetchedMatches.size > 0) {
                    matchDAO.insert(fetchedMatches)
                    fetchedMatches.clear()
                }

                clickedDAO.deleteIfMatched()
                preferenceProvider.putMatchFetchedAt(fetchedAt)
            }
            mutableFetchMatchesResource.postValue(matchResource)
        }
    }

    private fun setNewMatch(match: Match) {
        val currentOffsetDateTime = OffsetDateTime.now()
        match.unreadMessageCount = 1
        match.recentMessage = context.getString(R.string.default_recent_message)
        match.lastReadAt = currentOffsetDateTime
        match.lastReceivedAt = currentOffsetDateTime
    }

//  ################################################################################# //
//  ##################################### ACCOUNT ################################### //
//  ################################################################################# //

    private val mutableCards = MutableLiveData<Resource<List<CardResponse>>>()
    override val cards: LiveData<Resource<List<CardResponse>>>
        get() = mutableCards

    private var fetchingCards = false
    private var fetchedNoCards = false

    override fun fetchCards(reset: Boolean) {

        if (!fetchingCards && (reset || !fetchedNoCards)) {
            fetchingCards = true

            mutableCards.postValue(Resource.loading())

            CoroutineScope(Dispatchers.IO).launch {
                val accountId = preferenceProvider.getAccountId()
                val identityToken = preferenceProvider.getIdentityToken()
                val minAge = preferenceProvider.getMinAgeBirthYear()
                val maxAge = preferenceProvider.getMaxAgeBirthYear()
                val gender = preferenceProvider.getGender()
                val distance = preferenceProvider.getDistanceInMeters()
                var latitude: Double? = null
                var longitude: Double? = null
                var locationUpdatedAt: OffsetDateTime? = null

                val location: Location? = locationDAO.get()
                if (location != null && !location.synced) {
                    latitude = location.latitude
                    longitude = location.longitude
                    locationUpdatedAt = location.updatedAt
                }

                val cardsResource =
                    balanceRDS.fetchCards(
                        accountId,
                        identityToken,
                        minAge,
                        maxAge,
                        gender,
                        distance,
                        latitude,
                        longitude,
                        locationUpdatedAt?.toString(),
                        reset
                    )

                if (cardsResource.status == Resource.Status.SUCCESS) {

                    val fetchedCards = cardsResource.data

                    if (fetchedCards == null || fetchedCards.size == 0) {
                        fetchedNoCards = true
                    } else {

                        fetchedNoCards = false

                        val clickIds = clickDAO.getSwipedIds().toHashSet()
                        clickIds.addAll(matchDAO.getMatchedIds())

                        val endIndex = fetchedCards.size - 1

                        for (i in endIndex downTo 0) {
                            if (clickIds.contains(fetchedCards[i].accountId)) {
                                fetchedCards.removeAt(i)
                                continue
                            }
                            fetchedCards[i].birthYear =
                                Convert.birthYearToAge(fetchedCards[i].birthYear)
                        }
                    }

                    if (locationUpdatedAt != null)
                        locationDAO.sync(locationUpdatedAt)
                } else if (cardsResource.status == Resource.Status.EXCEPTION) {
                    fetchedNoCards = false
                }

                mutableCards.postValue(cardsResource)
                fetchingCards = false
            }
        }
    }

    override fun insertFCMToken(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val accountId = preferenceProvider.getAccountId()
            val identityToken = preferenceProvider.getIdentityToken()

            fcmTokenDAO.insert(FCMToken(token, false))

            val tokenResource = balanceRDS.postFCMToken(accountId, identityToken, token)
            if (tokenResource.status == Resource.Status.SUCCESS)
                fcmTokenDAO.sync()

        }
    }

    override fun saveLocation(latitude: Double, longitude: Double) {

        CoroutineScope(Dispatchers.IO).launch {

            val accountId = preferenceProvider.getAccountId()
            val identityToken = preferenceProvider.getIdentityToken()
            val updatedAt = OffsetDateTime.now()

            val currentLocation = Location(latitude, longitude, false, updatedAt)
            locationDAO.insert(currentLocation)

            val response =
                balanceRDS.postLocation(
                    accountId,
                    identityToken,
                    latitude,
                    longitude,
                    updatedAt.toString()
                )

            if (response.status == Resource.Status.SUCCESS)
                locationDAO.sync(updatedAt)
        }
    }

    override suspend fun saveAnswers(answers: Map<Int, Boolean>): Resource<EmptyJsonResponse> {
        return balanceRDS.saveAnswers(
            preferenceProvider.getAccountId(),
            preferenceProvider.getIdentityToken(),
            answers
        )
    }

    override suspend fun fetchQuestions(): Resource<List<QuestionResponse>> {
        return balanceRDS.fetchQuestions(
            preferenceProvider.getAccountId(),
            preferenceProvider.getIdentityToken()
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
            val accountId = preferenceProvider.getAccountId()
            val identityToken = preferenceProvider.getIdentityToken()

            val response = balanceRDS.fetchPhotos(accountId, identityToken)
            if (response.status == Resource.Status.EXCEPTION)
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
        photoUri: Uri?,
        sequence: Int
    ): Resource<EmptyJsonResponse> {
        photoUri?.let { uri ->
            uri.path?.let { path ->
                val photo = File(path)
                if (!fileExists(photo))
                    return Resource.exception(null, ExceptionCode.PHOTO_NOT_EXIST_EXCEPTION)

                val compressedPhoto = Compressor.compress(context, photo)
                if (compressedPhoto.length() > Photo.MAX_SIZE)
                    return Resource.exception(null, ExceptionCode.PHOTO_OUT_OF_SIZE_EXCEPTION)

                photoDAO.insert(Photo(photoKey, sequence, false))

                val fetchPreSignedUrlResponse = balanceRDS.addPhoto(
                    preferenceProvider.getAccountId(),
                    preferenceProvider.getIdentityToken(),
                    photoKey,
                    sequence
                )

                if (fetchPreSignedUrlResponse.isSuccess()) {
                    val preSignedUrl = fetchPreSignedUrlResponse.data!!
                    photoDAO.sync(photoKey, true)
                    val formData = mutableMapOf<String, RequestBody>()

                    for ((key, value) in preSignedUrl.fields) {
                        formData[key] = RequestBody.create(MultipartBody.FORM, value)
                    }

                    val mediaType = MediaType.parse(
                        MimeTypeMap.getSingleton().getMimeTypeFromExtension(photoExtension)!!
                    )

                    val requestBody = RequestBody.create(mediaType, compressedPhoto)
                    val multiPartBody =
                        MultipartBody.Part.createFormData("file", photoKey, requestBody)
                    val uploadPhotoToS3Response =
                        balanceRDS.uploadPhotoToS3(preSignedUrl.url, formData, multiPartBody)

                    if (uploadPhotoToS3Response.isSuccess()) {
                        deleteFile(compressedPhoto)
                        deleteFile(photo)
                    }
                    return uploadPhotoToS3Response
                }
                return Resource.exception(
                    fetchPreSignedUrlResponse.exceptionMessage,
                    fetchPreSignedUrlResponse.exceptionCode
                )
            }
        }
        return Resource.exception(null, ExceptionCode.PHOTO_NOT_EXIST_EXCEPTION)
    }

    private fun fileExists(file: File): Boolean {
        return try {
            file.exists()
        } catch (e: IOException) {
            // TODO: log exception?
            false
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

    override suspend fun deletePhoto(photoKey: String): Resource<EmptyJsonResponse> {
        val accountId = preferenceProvider.getAccountId()
        val identityToken = preferenceProvider.getIdentityToken()
        photoDAO.sync(photoKey, false)
        val response = balanceRDS.deletePhoto(accountId, identityToken, photoKey)
        if (response.isSuccess() || response.exceptionCode == ExceptionCode.PHOTO_NOT_FOUND_EXCEPTION)
            photoDAO.deletePhoto(photoKey)

        return response
    }

    override suspend fun reorderPhoto(photoOrders: Map<String, Int>): Resource<EmptyJsonResponse> {
        val accountId = preferenceProvider.getAccountId()
        val identityToken = preferenceProvider.getIdentityToken()

        for ((k, v) in photoOrders) {
            photoDAO.sync(k, false)
            photoDAO.updateSequence(k, v)
        }

        val response = balanceRDS.reorderPhotos(accountId, identityToken, photoOrders)
        if (response.isSuccess()) {
            for ((k, v) in photoOrders)
                photoDAO.sync(k, true)
        }
        return response
    }


//  ################################################################################# //
//  ##################################### MESSAGE ##################################### //
//  ################################################################################# //


    override suspend fun getMessages(chatId: Long): DataSource.Factory<Int, Message> {
        return withContext(Dispatchers.IO) {
            return@withContext messageDAO.getMessages(chatId)
        }
    }


    //  TEST 1. when you scroll up in the paged list and insert a new message, the list does not change because the new message is
//          out of screen.
//  TEST 2. when update all entries in database, it will update the items in list as well
    override fun insertMessage(chatId: Long) {
        GlobalScope.launch(Dispatchers.IO) {

            for (i in 1..2000) {
                val randomMessage = Random.nextInt(0, 100000)

                val message = Message(
                    null,
                    chatId,
                    "message - $randomMessage",
                    Random.nextBoolean(),
                    Random.nextBoolean(),
                    OffsetDateTime.now()
                )
                messageDAO.insert(message)
//                messageDAO.updateMessages()
            }
        }
    }

}




