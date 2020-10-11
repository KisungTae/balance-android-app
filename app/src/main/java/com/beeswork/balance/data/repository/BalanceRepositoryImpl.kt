package com.beeswork.balance.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.beeswork.balance.data.dao.ClickDAO
import com.beeswork.balance.data.dao.FCMTokenDAO
import com.beeswork.balance.data.dao.MatchDAO
import com.beeswork.balance.data.dao.MessageDAO
import com.beeswork.balance.data.entity.*
import com.beeswork.balance.data.network.response.Card
import com.beeswork.balance.data.network.rds.BalanceRDS
import com.beeswork.balance.data.network.response.BalanceGame
import com.beeswork.balance.data.provider.PreferenceProvider
import com.beeswork.balance.internal.Resource
import com.beeswork.balance.internal.constant.CURRENT_FCM_TOKEN_ID
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.converter.Convert
import kotlinx.coroutines.*
import org.threeten.bp.OffsetDateTime
import kotlin.random.Random


class BalanceRepositoryImpl(
    private val matchDAO: MatchDAO,
    private val messageDAO: MessageDAO,
    private val clickDAO: ClickDAO,
    private val fcmTokenDAO: FCMTokenDAO,
    private val balanceRDS: BalanceRDS,
    private val preferenceProvider: PreferenceProvider
) : BalanceRepository {

    private val mutableCards = MutableLiveData<Resource<List<Card>>>()
    override val cards: LiveData<Resource<List<Card>>>
        get() = mutableCards

    private val mutableBalanceGame = MutableLiveData<Resource<BalanceGame>>()
    override val balanceGame: LiveData<Resource<BalanceGame>>
        get() = mutableBalanceGame

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
                val distance = preferenceProvider.getDistance()

//                println("accountId: $accountId")
//                println("latitude: $latitude")
//                println("longitude: $longitude")
//                println("minAge: $minAge")
//                println("maxAge: $maxAge")
//                println("gender: $gender")
//                println("distance: $distance")


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
                    val matchedIds = matchDAO.getMatchedIds().toHashSet()
                    matchedIds.addAll(clickDAO.getSwipedIds())

                    val data = cardsResource.data!!
                    val endIndex = data.size - 1

                    for (i in endIndex downTo 0) {
                        data[i].birthYear = Convert.birthYearToAge(data[i].birthYear)
                        if (matchedIds.contains(data[i].accountId))
                            data.removeAt(i)
                    }
                }

                cardsBeingFetched = false
                mutableCards.postValue(cardsResource)
            }
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
                clickDAO.updatePosted(swipeId, true)
            } else if (clickResource.status == Resource.Status.EXCEPTION) {
                when (clickResource.exceptionCode) {
                    ExceptionCode.ACCOUNT_INVALID_EXCEPTION,
                    ExceptionCode.SWIPE_NOT_FOUND_EXCEPTION -> clickDAO.delete(swipeId)
                    ExceptionCode.MATCH_EXISTS_EXCEPTION -> clickDAO.updatePosted(swipeId, true)
                }
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
                fcmTokenDAO.updatePosted(CURRENT_FCM_TOKEN_ID, true)
            }
        }
    }

    override fun insertMatch() {

        GlobalScope.launch(Dispatchers.IO) {

            val matchedId = Random.nextInt(0, 100000)

            val match = Match(
                null,
                matchedId.toString(),
                "name - $matchedId",
                false,
                "recent message $matchedId",
                OffsetDateTime.now(),
                OffsetDateTime.now()
            )
            matchDAO.insert(match)
        }
    }

    override suspend fun getMatches(): LiveData<List<Match>> {
        return withContext(Dispatchers.IO) {
            return@withContext matchDAO.getMatches()
        }
    }

    override fun unmatch() {
        GlobalScope.launch(Dispatchers.IO) {
            matchDAO.unmatch("88277")
        }
    }


    override suspend fun getMessages(matchId: Int): DataSource.Factory<Int, Message> {
        return withContext(Dispatchers.IO) {
            return@withContext messageDAO.getMessages(matchId)
        }
    }

    override fun insertMessage() {
        GlobalScope.launch(Dispatchers.IO) {

            val randomMessage = Random.nextInt(0, 100000)

            val message = Message(
                null,
                2,
                Random.nextBoolean(),
                "message - $randomMessage",
                OffsetDateTime.now()
            )
            messageDAO.insert(message)
        }
    }

}




