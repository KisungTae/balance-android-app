package com.beeswork.balance.data.database.repository.swipe

import com.beeswork.balance.data.database.dao.LocationDAO
import com.beeswork.balance.data.database.dao.SwipeDAO
import com.beeswork.balance.data.database.dao.SwipeFilterDAO
import com.beeswork.balance.data.database.entity.Swipe
import com.beeswork.balance.data.database.entity.SwipeFilter
import com.beeswork.balance.data.network.rds.swipe.SwipeRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.swipe.CardDTO
import com.beeswork.balance.data.network.response.swipe.FetchCardsDTO
import com.beeswork.balance.internal.constant.Gender
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.*
import java.security.spec.RSAOtherPrimeInfo

class SwipeRepositoryImpl(
    private val preferenceProvider: PreferenceProvider,
    private val swipeFilterDAO: SwipeFilterDAO,
    private val swipeDAO: SwipeDAO,
    private val swipeRDS: SwipeRDS
) : SwipeRepository {

    override suspend fun getSwipeFilter(): SwipeFilter {
        return withContext(Dispatchers.IO) {
            return@withContext getSwipeFilterOrDefault()
        }
    }

    override suspend fun saveSwipeFilter(gender: Gender, minAge: Int, maxAge: Int, distance: Int) {
        withContext(Dispatchers.IO) {
            swipeFilterDAO.insert(SwipeFilter(gender, minAge, maxAge, distance))
        }
    }

    private fun getSwipeFilterOrDefault(): SwipeFilter {
        return swipeFilterDAO.findById() ?: kotlin.run {
            val defaultSwipeFilter = SwipeFilter()
            swipeFilterDAO.insert(defaultSwipeFilter)
            defaultSwipeFilter
        }
    }

    override suspend fun fetchCards(): Resource<FetchCardsDTO> {
        return withContext(Dispatchers.IO) {
            val swipeFilter = getSwipeFilterOrDefault()
            val response = swipeRDS.fetchCards(
                preferenceProvider.getAccountId(),
                preferenceProvider.getIdentityToken(),
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
                    val cardDTO = cardDTOs[i]
                    if (swipeDAO.existBySwipedId(cardDTO.accountId))
                        cardDTOs.removeAt(i)
                }
            }
            return@withContext response
        }
    }

    private suspend fun savePageIndex(currentPageIndex: Int, reset: Boolean) {
        withContext(Dispatchers.IO) {
            var pageIndex = if (reset) 0 else currentPageIndex
            pageIndex++
            swipeFilterDAO.updatePageIndex(pageIndex)
        }
    }
}