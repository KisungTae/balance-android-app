package com.beeswork.balance.data.database.repository.card

import com.beeswork.balance.data.database.dao.ClickDAO
import com.beeswork.balance.data.database.dao.CardFilterDAO
import com.beeswork.balance.data.database.entity.card.CardFilter
import com.beeswork.balance.data.network.rds.card.CardRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.card.FetchCardsDTO
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.profile.FetchQuestionsDTO
import com.beeswork.balance.internal.exception.AccountIdNotFoundException
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import java.io.IOException
import java.util.*

class CardRepositoryImpl(
    private val preferenceProvider: PreferenceProvider,
    private val cardFilterDAO: CardFilterDAO,
    private val clickDAO: ClickDAO,
    private val cardRDS: CardRDS,
    private val ioDispatcher: CoroutineDispatcher
) : CardRepository {

    override suspend fun like(swipedId: UUID): Resource<FetchQuestionsDTO> {
        return withContext(ioDispatcher) {
            return@withContext cardRDS.like(swipedId)
        }
    }

    override suspend fun deleteCardFilter() {
        withContext(ioDispatcher) {
            cardFilterDAO.deleteAllBy(preferenceProvider.getAccountId())
        }
    }

    override suspend fun getCardFilter(): CardFilter? {
        return withContext(ioDispatcher) {
            return@withContext cardFilterDAO.getBy(preferenceProvider.getAccountId())
        }
    }

    override suspend fun saveCardFilter(gender: Boolean, minAge: Int, maxAge: Int, distance: Int): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            val accountId = preferenceProvider.getAccountId() ?: return@withContext Resource.error(AccountIdNotFoundException())
            val cardFilter = cardFilterDAO.getBy(accountId)
            if (cardFilter == null) {
                cardFilterDAO.insert(CardFilter(accountId, gender, minAge, maxAge, distance))
            } else if (!cardFilter.isEqualTo(gender, minAge, maxAge, distance)) {
                cardFilter.gender = gender
                cardFilter.minAge = minAge
                cardFilter.maxAge = maxAge
                cardFilter.distance = distance
                cardFilterDAO.insert(cardFilter)
            }
            return@withContext Resource.success(EmptyResponse())
        }
    }

    override suspend fun fetchCards(): Resource<FetchCardsDTO> {
        return withContext(ioDispatcher) {
//            val accountId = preferenceProvider.getAccountId()
//            val swipeFilter = cardFilterDAO.getBy(accountId)
//            val response = cardRDS.fetchCards(
//                swipeFilter.minAge,
//                swipeFilter.maxAge,
//                swipeFilter.gender,
//                swipeFilter.distance,
//                swipeFilter.pageIndex
//            )
//            response.data?.let { data ->
//                savePageIndex(swipeFilter.pageIndex, data.reset)
//                val cardDTOs = data.cardDTOs
//                for (i in cardDTOs.size - 1 downTo 0) {
//                    if (clickDAO.existBy(accountId, cardDTOs[i].accountId))
//                        cardDTOs.removeAt(i)
//                }
//                cardDTOs.shuffle()
//            }
//            return@withContext response
            return@withContext Resource.error(IOException())
        }
    }

    override suspend fun prepopulateCardFilter(gender: Boolean) {
        withContext(ioDispatcher) {
//            val accountId = preferenceProvider.getAccountId() ?: return@withContext
//            if (!cardFilterDAO.existBy(accountId)) {
//                cardFilterDAO.insert(CardFilter(accountId, Gender.getOppositeGender(gender)))
//            }
        }
    }

    private suspend fun savePageIndex(currentPageIndex: Int, reset: Boolean) {
        withContext(ioDispatcher) {
            var pageIndex = if (reset) 0 else currentPageIndex
            pageIndex++
            cardFilterDAO.updatePageIndexBy(preferenceProvider.getAccountId(), pageIndex)
        }
    }

    override fun getCardFilterInvalidationFlow(): Flow<Boolean?> {
        return cardFilterDAO.getCardFilterGenderFlow(preferenceProvider.getAccountId())
    }


}