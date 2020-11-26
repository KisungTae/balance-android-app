package com.beeswork.balance.data.database.repository

import android.content.Context
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
import com.beeswork.balance.internal.constant.NotificationType
import com.beeswork.balance.internal.converter.Convert
import kotlinx.coroutines.*
import org.threeten.bp.OffsetDateTime
import kotlin.random.Random


class BalanceRepositoryImpl(
    private val context: Context,
    private val matchDAO: MatchDAO,
    private val messageDAO: MessageDAO,
    private val clickDAO: ClickDAO,
    private val fcmTokenDAO: FCMTokenDAO,
    private val clickedDAO: ClickedDAO,
    private val profileDAO: ProfileDAO,
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

            val clickResource = balanceRDS.click(accountId, identityToken, swipedId, swipeId, answers)

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
                        matchDAO.update(fetchedMatch.chatId, fetchedMatch.photoKey, fetchedMatch.unmatched)
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
                            fetchedCards[i].birthYear = Convert.birthYearToAge(fetchedCards[i].birthYear)
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
                balanceRDS.postLocation(accountId, identityToken, latitude, longitude, updatedAt.toString())

            if (response.status == Resource.Status.SUCCESS)
                locationDAO.sync(updatedAt)
        }
    }

    private val mutableQuestions = MutableLiveData<Resource<List<QuestionResponse>>>()
    override val questions: LiveData<Resource<List<QuestionResponse>>>
        get() = mutableQuestions

    override fun fetchQuestions() {

        CoroutineScope(Dispatchers.IO).launch {
            val accountId = preferenceProvider.getAccountId()
            val identityToken = preferenceProvider.getIdentityToken()
            mutableQuestions.postValue(Resource.loading())
            val response = balanceRDS.fetchQuestions(accountId, identityToken)
            mutableQuestions.postValue(response)
        }
    }


    private val mutableSaveAnswers = MutableLiveData<Resource<EmptyJsonResponse>>()
    override val saveAnswers: LiveData<Resource<EmptyJsonResponse>>
        get() = mutableSaveAnswers

    override fun saveAnswers(answers: Map<Int, Boolean>) {

        CoroutineScope(Dispatchers.IO).launch {
            val accountId = preferenceProvider.getAccountId()
            val identityToken = preferenceProvider.getIdentityToken()
            mutableSaveAnswers.postValue(Resource.loading())
            val response = balanceRDS.saveAnswers(accountId, identityToken, answers)
            mutableSaveAnswers.postValue(response)
        }
    }

    private val mutableFetchRandomQuestion = MutableLiveData<Resource<QuestionResponse>>()
    override val fetchRandomQuestion: LiveData<Resource<QuestionResponse>>
        get() = mutableFetchRandomQuestion

    override fun fetchRandomQuestion(questionIds: List<Int>) {

        CoroutineScope(Dispatchers.IO).launch {
            mutableFetchRandomQuestion.postValue(Resource.loading())
            val response = balanceRDS.fetchRandomQuestion(questionIds)
            mutableFetchRandomQuestion.postValue(response)
        }
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



