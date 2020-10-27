package com.beeswork.balance.data.database.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.beeswork.balance.R
import com.beeswork.balance.data.database.dao.*
import com.beeswork.balance.data.database.entity.*
import com.beeswork.balance.data.network.response.Card
import com.beeswork.balance.data.network.rds.BalanceRDS
import com.beeswork.balance.internal.provider.PreferenceProvider
import com.beeswork.balance.internal.Resource
import com.beeswork.balance.internal.constant.CURRENT_FCM_TOKEN_ID
import com.beeswork.balance.internal.constant.ExceptionCode
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

    private val mutableBalanceGame = MutableLiveData<Resource<BalanceGame>>()
    override val balanceGame: LiveData<Resource<BalanceGame>>
        get() = mutableBalanceGame

    private val mutableFetchClickedResource = MutableLiveData<Resource<List<Clicked>>>()
    override val fetchClickedListResource: LiveData<Resource<List<Clicked>>>
        get() = mutableFetchClickedResource

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
            mutableFetchClickedResource.postValue(clickedResource)
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

    override fun swipe(swipedId: String) {

        CoroutineScope(Dispatchers.IO).launch {
            val accountId = preferenceProvider.getAccountId()
            val email = preferenceProvider.getEmail()

            mutableBalanceGame.postValue(Resource.loading())
            val questionResource = balanceRDS.swipe(accountId, email, swipedId)
            mutableBalanceGame.postValue(questionResource)
        }
    }

    //  TEST 1. even if you leave the app before completing the network call, when you come back to the app
    //          you will get the response. The response is received in the background
    override fun click(swipedId: String, swipeId: Long) {

        CoroutineScope(Dispatchers.IO).launch {
            val accountId = preferenceProvider.getAccountId()
            val email = preferenceProvider.getEmail()

            clickDAO.insert(Click(swipeId, swipedId, false, OffsetDateTime.now()))
            val clickResource = balanceRDS.click(accountId, email, swipedId, swipeId)

            if (clickResource.status == Resource.Status.SUCCESS) {
                clickDAO.update(swipeId, true)
            } else if (clickResource.status == Resource.Status.EXCEPTION) {
                when (clickResource.exceptionCode) {
                    ExceptionCode.ACCOUNT_INVALID_EXCEPTION,
                    ExceptionCode.SWIPE_NOT_FOUND_EXCEPTION -> clickDAO.delete(swipeId)
                    ExceptionCode.MATCH_EXISTS_EXCEPTION -> clickDAO.update(swipeId, true)
                }
            }
        }
    }


//  ################################################################################# //
//  ##################################### MATCH ##################################### //
//  ################################################################################# //


    private val mutableFetchMatchesResource = MutableLiveData<Resource<List<Match>>>()
    override val fetchMatchesResource: LiveData<Resource<List<Match>>>
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

                    if (matchDAO.existsByChatId(fetchedMatch.chatId)) {
                        matchDAO.update(
                            fetchedMatch.chatId,
                            fetchedMatch.photoKey,
                            fetchedMatch.unmatched
                        )
                        fetchedMatches.removeAt(i)
                    } else {
                        val currentOffsetDateTime = OffsetDateTime.now()
                        fetchedMatch.unreadMessageCount = 1
                        fetchedMatch.recentMessage = context.getString(R.string.default_recent_message)
                        fetchedMatch.lastReadAt = currentOffsetDateTime
                        fetchedMatch.lastReceivedAt = currentOffsetDateTime
                    }
                }

                if (fetchedMatches.size > 0) {
                    matchDAO.insert(fetchedMatches)
                    fetchedMatches.clear()
                }

                clickedDAO.syncWithMatch()
                preferenceProvider.putMatchFetchedAt(fetchedAt)
            }
            mutableFetchMatchesResource.postValue(matchResource)
        }
    }



//  ################################################################################# //
//  ##################################### ACCOUNT ################################### //
//  ################################################################################# //

    private val mutableCards = MutableLiveData<Resource<List<Card>>>()
    override val cards: LiveData<Resource<List<Card>>>
        get() = mutableCards

    private var cardsBeingFetched = false

    override fun fetchCards() {

        if (!cardsBeingFetched) {
            cardsBeingFetched = true

            CoroutineScope(Dispatchers.IO).launch {
                val accountId = preferenceProvider.getAccountId()
                val latitude = preferenceProvider.getLatitude()
                val longitude = preferenceProvider.getLongitude()
                val minAge = preferenceProvider.getMinAgeBirthYear()
                val maxAge = preferenceProvider.getMaxAgeBirthYear()
                val gender = preferenceProvider.getGender()
                val distance = preferenceProvider.getDistanceInMeters()


                val cardsResource =
                    balanceRDS.fetchCards(
                        accountId,
                        latitude,
                        longitude,
                        minAge,
                        maxAge,
                        gender,
                        distance
                    )

                if (cardsResource.status == Resource.Status.SUCCESS) {

                    val clickIds = clickDAO.getSwipedIds().toHashSet()
                    clickIds.addAll(matchDAO.getMatchedIds())

                    val fetchedCards = cardsResource.data!!
                    val endIndex = fetchedCards.size - 1

                    for (i in endIndex downTo 0) {
                        fetchedCards[i].birthYear =
                            Convert.birthYearToAge(fetchedCards[i].birthYear)
                        if (clickIds.contains(fetchedCards[i].accountId))
                            fetchedCards.removeAt(i)
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

            fcmTokenDAO.insert(FCMToken(token, false, OffsetDateTime.now()))

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




