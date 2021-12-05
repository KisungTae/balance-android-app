package com.beeswork.balance.data.database.repository.swipe

import com.beeswork.balance.data.database.dao.SwipeDAO
import com.beeswork.balance.data.database.dao.SwipeFilterDAO
import com.beeswork.balance.data.database.entity.swipe.SwipeFilter
import com.beeswork.balance.data.network.rds.swipe.SwipeRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.profile.QuestionDTO
import com.beeswork.balance.data.network.response.swipe.FetchCardsDTO
import com.beeswork.balance.internal.constant.Gender
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.*
import java.util.*

class SwipeRepositoryImpl(
    private val preferenceProvider: PreferenceProvider,
    private val swipeFilterDAO: SwipeFilterDAO,
    private val swipeDAO: SwipeDAO,
    private val swipeRDS: SwipeRDS,
    private val ioDispatcher: CoroutineDispatcher
) : SwipeRepository {
    override suspend fun deleteSwipes() {
        withContext(ioDispatcher) {
            swipeDAO.deleteAll(preferenceProvider.getAccountId())
            swipeFilterDAO.deleteAll(preferenceProvider.getAccountId())
        }
    }

    override suspend fun getSwipeFilter(): SwipeFilter {
        return withContext(ioDispatcher) {
            return@withContext swipeFilterDAO.findById(preferenceProvider.getAccountId())
        }
    }

    override suspend fun saveSwipeFilter(gender: Boolean, minAge: Int, maxAge: Int, distance: Int) {
        withContext(ioDispatcher) {
            swipeFilterDAO.update(
                preferenceProvider.getAccountId(),
                gender,
                if (minAge < SwipeFilter.MIN_AGE) SwipeFilter.MIN_AGE else minAge,
                if (maxAge > SwipeFilter.MAX_AGE) SwipeFilter.MAX_AGE else maxAge,
                if (distance < SwipeFilter.MIN_DISTANCE || distance > SwipeFilter.MAX_DISTANCE) SwipeFilter.MAX_DISTANCE else distance
            )
        }
    }

    override suspend fun fetchCards(): Resource<FetchCardsDTO> {
        return withContext(ioDispatcher) {
            val accountId = preferenceProvider.getAccountId()
            val swipeFilter = swipeFilterDAO.findById(accountId)
            val response = swipeRDS.fetchCards(
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
                    if (swipeDAO.existBySwipedId(accountId, cardDTOs[i].accountId))
                        cardDTOs.removeAt(i)
                }
                cardDTOs.shuffle()
            }
            return@withContext response
        }
    }

    override suspend fun swipe(swipedId: UUID): Resource<List<QuestionDTO>> {
        return withContext(ioDispatcher) {
            return@withContext swipeRDS.swipe(swipedId)
        }
    }

    override suspend fun prepopulateSwipeFilter(gender: Boolean) {
        withContext(ioDispatcher) {
            preferenceProvider.getAccountId()?.let { accountId ->
                if (!swipeFilterDAO.existByAccountId(accountId))
                    swipeFilterDAO.insert(SwipeFilter(accountId, Gender.getOppositeGender(gender)))
            }
        }
    }

    private suspend fun savePageIndex(currentPageIndex: Int, reset: Boolean) {
        withContext(ioDispatcher) {
            var pageIndex = if (reset) 0 else currentPageIndex
            pageIndex++
            swipeFilterDAO.updatePageIndex(preferenceProvider.getAccountId(), pageIndex)
        }
    }
}