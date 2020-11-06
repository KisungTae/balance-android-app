package com.beeswork.balance.data.database.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.beeswork.balance.R
import com.beeswork.balance.data.database.dao.*
import com.beeswork.balance.data.database.entity.*
import com.beeswork.balance.data.network.response.CardResponse
import com.beeswork.balance.data.network.rds.BalanceRDS
import com.beeswork.balance.data.network.response.BalanceGameResponse
import com.beeswork.balance.data.network.response.ClickResponse
import com.beeswork.balance.internal.provider.PreferenceProvider
import com.beeswork.balance.internal.Resource
import com.beeswork.balance.internal.constant.CURRENT_FCM_TOKEN_ID
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
    private val balanceRDS: BalanceRDS,
    private val preferenceProvider: PreferenceProvider
) : BalanceRepository {

//  ################################################################################# //
//  ##################################### SWIPE ##################################### //
//  ################################################################################# //

    private val mutableFetchClickedList = MutableLiveData<Resource<List<Clicked>>>()
    override val fetchClickedListResponse: LiveData<Resource<List<Clicked>>>
        get() = mutableFetchClickedList

    override fun fetchClickedList() {

        CoroutineScope(Dispatchers.IO).launch {

            val clickedResource = balanceRDS.fetchClickedList(
                preferenceProvider.getAccountId(),
                preferenceProvider.getEmail(),
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
            mutableFetchClickedList.postValue(clickedResource)
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

    override fun swipe(swipedId: String) {

        CoroutineScope(Dispatchers.IO).launch {
            val accountId = preferenceProvider.getAccountId()
            val email = preferenceProvider.getEmail()

            mutableBalanceGameResponse.postValue(Resource.loading())
            val questionResource = balanceRDS.swipe(accountId, email, swipedId)
            mutableBalanceGameResponse.postValue(questionResource)
        }
    }

    private val mutableClickResponse = MutableLiveData<Resource<ClickResponse>>()
    override val clickResponse: LiveData<Resource<ClickResponse>>
        get() = mutableClickResponse

    //  TEST 1. even if you leave the app before completing the network call, when you come back to the app
    //          you will get the response. The response is received in the background
    override fun click(swipedId: String, swipeId: Long, answers: Map<Long, Boolean>) {

        CoroutineScope(Dispatchers.IO).launch {
            val accountId = preferenceProvider.getAccountId()
            val email = preferenceProvider.getEmail()

            val clickResource = balanceRDS.click(accountId, email, swipedId, swipeId, answers)

            if (clickResource.status == Resource.Status.SUCCESS) {
                val data = clickResource.data!!
                val notificationType = data.notificationType
                val match = data.match

                if (notificationType == NotificationType.MATCH) {
                    setNewMatch(match)
                    matchDAO.insert(match)
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
                preferenceProvider.getEmail(),
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

    private var cardsBeingFetched = false

    override fun fetchCards() {

        if (!cardsBeingFetched) {
            cardsBeingFetched = true

            mutableCards.postValue(Resource.loading())

            CoroutineScope(Dispatchers.IO).launch {
                val accountId = preferenceProvider.getAccountId()
                val email = preferenceProvider.getEmail()
                val latitude = preferenceProvider.getLatitude()
                val longitude = preferenceProvider.getLongitude()
                val minAge = preferenceProvider.getMinAgeBirthYear()
                val maxAge = preferenceProvider.getMaxAgeBirthYear()
                val gender = preferenceProvider.getGender()
                val distance = preferenceProvider.getDistanceInMeters()


                val cardsResource =
                    balanceRDS.fetchCards(accountId, email, latitude, longitude, minAge, maxAge, gender, distance)

                if (cardsResource.status == Resource.Status.SUCCESS) {

                    val clickIds = clickDAO.getSwipedIds().toHashSet()
                    clickIds.addAll(matchDAO.getMatchedIds())

                    val fetchedCards = cardsResource.data!!
                    val endIndex = fetchedCards.size - 1

                    for (i in endIndex downTo 0) {
                        if (clickIds.contains(fetchedCards[i].accountId)) {
                            fetchedCards.removeAt(i)
                            continue
                        }
                        fetchedCards[i].birthYear = Convert.birthYearToAge(fetchedCards[i].birthYear)
                    }
                }

                cardsBeingFetched = false
                mutableCards.postValue(cardsResource)
            }
        }
    }

    override fun insertFCMToken(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val accountId = preferenceProvider.getAccountId()
            val email = preferenceProvider.getEmail()

            fcmTokenDAO.insert(FCMToken(token, false))

            val tokenResource = balanceRDS.postFCMToken(accountId, email, token)
            if (tokenResource.status == Resource.Status.SUCCESS) {
                fcmTokenDAO.update(CURRENT_FCM_TOKEN_ID, true)
            }
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




