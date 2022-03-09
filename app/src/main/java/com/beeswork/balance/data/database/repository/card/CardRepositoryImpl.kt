package com.beeswork.balance.data.database.repository.card

import com.beeswork.balance.data.database.dao.ClickDAO
import com.beeswork.balance.data.database.dao.CardFilterDAO
import com.beeswork.balance.data.database.entity.card.CardFilter
import com.beeswork.balance.data.network.rds.card.CardRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.profile.QuestionDTO
import com.beeswork.balance.data.network.response.card.FetchCardsDTO
import com.beeswork.balance.internal.constant.Gender
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.*
import java.util.*

class CardRepositoryImpl(
    private val preferenceProvider: PreferenceProvider,
    private val cardFilterDAO: CardFilterDAO,
    private val clickDAO: ClickDAO,
    private val cardRDS: CardRDS,
    private val ioDispatcher: CoroutineDispatcher
) : CardRepository {

    override suspend fun like(swipedId: UUID): Resource<List<QuestionDTO>> {
        return withContext(ioDispatcher) {
            return@withContext cardRDS.like(swipedId)
        }
    }

    override suspend fun deleteCardFilter() {
        withContext(ioDispatcher) {
            cardFilterDAO.deleteAllBy(preferenceProvider.getAccountId())
        }
    }

    override suspend fun getCardFilter(): CardFilter {
        return withContext(ioDispatcher) {
            return@withContext cardFilterDAO.getBy(preferenceProvider.getAccountId())
        }
    }

    override suspend fun saveCardFilter(gender: Boolean, minAge: Int, maxAge: Int, distance: Int) {
        withContext(ioDispatcher) {
            cardFilterDAO.updateBy(
                preferenceProvider.getAccountId(),
                gender,
                if (minAge < CardFilter.MIN_AGE) CardFilter.MIN_AGE else minAge,
                if (maxAge > CardFilter.MAX_AGE) CardFilter.MAX_AGE else maxAge,
                if (distance < CardFilter.MIN_DISTANCE || distance > CardFilter.MAX_DISTANCE) CardFilter.MAX_DISTANCE else distance
            )
        }
    }

    override suspend fun fetchCards(): Resource<FetchCardsDTO> {
        return withContext(ioDispatcher) {
            val accountId = preferenceProvider.getAccountId()
            val swipeFilter = cardFilterDAO.getBy(accountId)
            val response = cardRDS.fetchCards(
                swipeFilter.minAge,
                swipeFilter.maxAge,
                swipeFilter.gender,
                swipeFilter.distance,
                swipeFilter.pageIndex
            )
            response.data?.let { data ->
                savePageIndex(swipeFilter.pageIndex, data.reset)
                val cardDTOs = data.cardDTOs
                for (i in cardDTOs.size - 1 downTo 0) {
                    if (clickDAO.existBy(accountId, cardDTOs[i].accountId))
                        cardDTOs.removeAt(i)
                }
                cardDTOs.shuffle()
            }
            return@withContext response
        }
    }

    override suspend fun prepopulateCardFilter(gender: Boolean) {
        withContext(ioDispatcher) {
            preferenceProvider.getAccountId()?.let { accountId ->
                if (!cardFilterDAO.existBy(accountId)) {
                    cardFilterDAO.insert(CardFilter(accountId, Gender.getOppositeGender(gender)))
                }
            }
        }
    }

    private suspend fun savePageIndex(currentPageIndex: Int, reset: Boolean) {
        withContext(ioDispatcher) {
            var pageIndex = if (reset) 0 else currentPageIndex
            pageIndex++
            cardFilterDAO.updatePageIndexBy(preferenceProvider.getAccountId(), pageIndex)
        }
    }
}