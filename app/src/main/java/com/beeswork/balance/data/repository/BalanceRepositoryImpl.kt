package com.beeswork.balance.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.beeswork.balance.data.dao.ClickDAO
import com.beeswork.balance.data.dao.FirebaseMessagingTokenDAO
import com.beeswork.balance.data.dao.MatchDAO
import com.beeswork.balance.data.dao.MessageDAO
import com.beeswork.balance.data.entity.Click
import com.beeswork.balance.data.entity.FirebaseMessagingToken
import com.beeswork.balance.data.network.response.Card
import com.beeswork.balance.data.entity.Match
import com.beeswork.balance.data.entity.Message
import com.beeswork.balance.data.network.rds.BalanceRDS
import com.beeswork.balance.data.network.response.BalanceGame
import com.beeswork.balance.data.provider.PreferenceProvider
import com.beeswork.balance.internal.Resource
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.constant.FirebaseMessagingTokenConstant
import com.beeswork.balance.internal.converter.Convert
import kotlinx.coroutines.*
import org.threeten.bp.OffsetDateTime
import kotlin.random.Random


class BalanceRepositoryImpl(
    private val matchDAO: MatchDAO,
    private val messageDAO: MessageDAO,
    private val clickDAO: ClickDAO,
    private val firebaseMessagingTokenDAO: FirebaseMessagingTokenDAO,
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
                    balanceRDS.fetchCards(accountId,
                                          latitude,
                                          longitude,
                                          minAge,
                                          maxAge,
                                          gender,
                                          distance)

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

    override fun insertFirebaseMessagingToken(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val accountId = preferenceProvider.getAccountId()
            val email = preferenceProvider.getEmail()

            val toke = firebaseMessagingTokenDAO.get()

            firebaseMessagingTokenDAO.insert(FirebaseMessagingToken(token, false, OffsetDateTime.now()))
            val tokenResource = balanceRDS.postFirebaseMessagingToken(accountId, email, token)
            if (tokenResource.status == Resource.Status.SUCCESS) {
                firebaseMessagingTokenDAO.updatePosted(FirebaseMessagingTokenConstant.id, true)
            }


        }
    }

    override fun insertMatch() {

        GlobalScope.launch(Dispatchers.IO) {

            val matchedId = Random.nextInt(0, 100000)


            val ids = mutableListOf(
                "456cb6c4-91e3-4b9d-8dda-062306154c6f",
                "743e394b-9779-437e-8028-769994e08582",
                "65efa75d-2427-42bb-b8d4-014e39a1676e",
                "c0ca134c-9fd1-4cc5-a8cc-2fb424a9ece5",
                "6115de7b-c18c-4cf9-bca1-b707c2074438",
                "64bf31be-35c9-451b-8f63-42147e2d69ca",
                "12ad4ace-f398-4762-b077-20be7c39a088",
                "e05f7775-7177-489d-9baf-78287ba4e8d9",
                "b5e8f581-eb5f-42e8-9751-b1fd81908eb7",
                "9e10a345-f96c-41ae-8028-f25f962f24a4",
                "3b89913e-ad48-4179-bee7-500adaca19df",
                "880e8f70-852a-411a-8f8d-1a0aa5b0a4fb",
                "87d81a6c-ba4c-4c1d-94ea-288b883fb840",
                "ad48930a-c7de-4901-880b-329eac5a44a6",
                "81b5fac0-e232-46c0-934f-697f39d2d7a5")


            val match = Match(null,
                              matchedId.toString(),
                              "name - $matchedId",
                              false,
                              "recent message $matchedId",
                              OffsetDateTime.now(),
                              OffsetDateTime.now())
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

            val message = Message(null,
                                  2,
                                  Random.nextBoolean(),
                                  "message - $randomMessage",
                                  OffsetDateTime.now())
            messageDAO.insert(message)
        }
    }

}




