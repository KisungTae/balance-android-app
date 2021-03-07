package com.beeswork.balance.data.database.repository

import android.content.Context
import android.webkit.MimeTypeMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.beeswork.balance.R
import com.beeswork.balance.data.database.dao.*
import com.beeswork.balance.data.database.entity.*
import com.beeswork.balance.data.network.rds.BalanceRDS
import com.beeswork.balance.data.network.response.*
import com.beeswork.balance.ui.chat.ChatMessageEvent
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.constant.NotificationType
import com.beeswork.balance.internal.util.Convert
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
    private val clickedDAO: ClickedDAO,
    private val fcmTokenDAO: FCMTokenDAO,
    private val clickerDAO: ClickerDAO,
    private val profileDAO: ProfileDAO,
    private val photoDAO: PhotoDAO,
    private val locationDAO: LocationDAO,
    private val balanceRDS: BalanceRDS,
    private val preferenceProvider: PreferenceProvider
) : BalanceRepository {

//  ################################################################################# //
//  ##################################### SWIPE ##################################### //
//  ################################################################################# //

    private val mutableFetchClickedListResponse = MutableLiveData<Resource<List<Clicker>>>()
    override val fetchClickerListResponse: LiveData<Resource<List<Clicker>>>
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

    override suspend fun getClickedCount(): LiveData<Int> {
        return withContext(Dispatchers.IO) {
            return@withContext clickerDAO.count()
        }
    }

    override suspend fun getClickedList(): DataSource.Factory<Int, Clicker> {
        return withContext(Dispatchers.IO) {
            return@withContext clickerDAO.getClickers()
        }
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

    override suspend fun getMatches(): DataSource.Factory<Int, Match> {
        return withContext(Dispatchers.IO) {
            return@withContext matchDAO.findAllPaged()
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

    private val mutableCards = MutableLiveData<Resource<List<CardResponse>>>()
    override val cards: LiveData<Resource<List<CardResponse>>>
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

    override fun insertFCMToken(token: String) {
//        CoroutineScope(Dispatchers.IO).launch {
//            val accountId = preferenceProvider.getAccountId1()
//            val identityToken = preferenceProvider.getIdentityToken1()
//
//            fcmTokenDAO.insert(FCMToken(token, false))
//
//            val tokenResource = balanceRDS.postFCMToken(accountId, identityToken, token)
//            if (tokenResource.status == Resource.Status.SUCCESS)
//                fcmTokenDAO.sync()
//
//        }
    }

    override fun saveLocation(latitude: Double, longitude: Double) {

//        CoroutineScope(Dispatchers.IO).launch {
//
//            val accountId = preferenceProvider.getAccountId1()
//            val identityToken = preferenceProvider.getIdentityToken1()
//            val updatedAt = OffsetDateTime.now()
//
//            val currentLocation = Location(latitude, longitude, false, updatedAt)
//            locationDAO.insert(currentLocation)
//
//            val response =
//                balanceRDS.postLocation(
//                    accountId,
//                    identityToken,
//                    latitude,
//                    longitude,
//                    updatedAt.toString()
//                )
//
//            if (response.status == Resource.Status.SUCCESS)
//                locationDAO.sync(updatedAt)
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

    private val mutableChatMessageEvent = MutableLiveData<ChatMessageEvent>()
    override val chatMessageEvent: LiveData<ChatMessageEvent>
        get() = mutableChatMessageEvent

    override suspend fun getChatMessages(chatId: Long): DataSource.Factory<Int, ChatMessage> {
        return withContext(Dispatchers.IO) {
            return@withContext chatMessageDAO.getChatMessages(chatId)
        }
    }

//    override fun getChatMessages(
//        chatId: Long
//    ): PagingSource<Int, ChatMessage> {
//        return chatMessageDAO.getChatMessages(chatId)
//    }

    //  TEST 1. when you scroll up in the paged list and insert a new message, the list does not change because the new message is
    //          out of screen.
    //  TEST 2. when update all entries in database, it will update the items in list as well
    override suspend fun saveChatMessage(
        chatId: Long,
        body: String
    ): Long {
//        val chatMessages = chatMessageDAO.findAllAfter(chatId, 200, 100)
//        val chatMessagesPre = chatMessageDAO.findAllBefore(chatId, 200, 10000)

//        val matches = balanceRDS.fetchMatches(
//            preferenceProvider.getAccountId1(),
//            preferenceProvider.getIdentityToken1(),
//            preferenceProvider.getMatchFetchedAt1(),
//            preferenceProvider.getMatchFetchedAt1(),
//            preferenceProvider.getMatchFetchedAt1()
//        )

        return 1L
    }

    override fun getMessages(chatId: Long): List<ChatMessage> {
        return chatMessageDAO.findAllAfter(chatId, 1, 2)
    }

    override suspend fun syncMessage(
        chatId: Long,
        messageId: Long,
        id: Long,
        createdAt: OffsetDateTime
    ) {
        chatMessageDAO.sync(chatId, messageId, id, createdAt, ChatMessageStatus.SENT)
    }

    override suspend fun fetchChatMessages(
        chatId: Long,
        recipientId: String,
        pageSize: Int
    ): Resource<List<ChatMessage>> {
        if (matchDAO.isUnmatched(chatId)) return Resource.success(
            initializeChatMessages(
                chatId,
                pageSize
            )
        )

//        val response = balanceRDS.fetchChatMessages(
//            preferenceProvider.getAccountId1(),
//            preferenceProvider.getIdentityToken1(),
//            chatId,
//            recipientId,
//            chatMessageDAO.findLastId(chatId) ?: 0
//        )

//        if (response.isError()) return response
//
//        response.data?.let { chatMessages ->
//            for (i in chatMessages.indices) {
//                val chatMessage = chatMessages[i]
//                chatMessage.status = if (chatMessage.messageId == null) ChatMessageStatus.RECEIVED else ChatMessageStatus.SENT
//            }
//            chatMessageDAO.insert(chatMessages)
//        }

        return Resource.success(
            initializeChatMessages(
                chatId,
                pageSize
            )
        )
    }

    private fun initializeChatMessages(chatId: Long, pageSize: Int): List<ChatMessage> {
        chatMessageDAO.updateStatus(chatId, ChatMessageStatus.SENDING, ChatMessageStatus.ERROR)
        val chatMessages = chatMessageDAO.findAllRecent(chatId, pageSize)
        chatMessages.addAll(0, chatMessageDAO.findAllUnprocessed(chatId))
        return chatMessages
    }

    override suspend fun appendChatMessages(
        chatId: Long,
        lastChatMessageId: Long,
        pageSize: Int
    ): List<ChatMessage> {
        return chatMessageDAO.findAllBefore(chatId, lastChatMessageId, pageSize)
    }

    override suspend fun prependChatMessages(
        chatId: Long,
        firstChatMessageId: Long,
        pageSize: Int
    ): List<ChatMessage> {
        val chatMessages = chatMessageDAO.findAllAfter(chatId, firstChatMessageId, pageSize)
        if (chatMessages.size < pageSize) chatMessages.addAll(
            0,
            chatMessageDAO.findAllUnprocessed(chatId)
        )
        return chatMessages
    }


}




