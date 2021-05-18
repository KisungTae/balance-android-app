package com.beeswork.balance.data.database.repository.swipe

import com.beeswork.balance.data.database.dao.SwipeDAO
import com.beeswork.balance.data.database.dao.SwipeFilterDAO
import com.beeswork.balance.data.database.entity.SwipeFilter
import com.beeswork.balance.data.network.rds.swipe.SwipeRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.swipe.FetchCardsDTO
import com.beeswork.balance.internal.constant.Gender
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.*
import java.util.*

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
            val swipeFilter = SwipeFilter(
                gender,
                if (minAge < SwipeFilter.MIN_AGE) SwipeFilter.MIN_AGE else minAge,
                if (maxAge > SwipeFilter.MAX_AGE) SwipeFilter.MAX_AGE else minAge,
                if (distance < SwipeFilter.MIN_DISTANCE || distance > SwipeFilter.MAX_DISTANCE) SwipeFilter.MAX_DISTANCE else distance
            )
            swipeFilterDAO.insert(swipeFilter)
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
                yearFromAge(swipeFilter.minAge),
                yearFromAge(swipeFilter.maxAge),
                swipeFilter.gender,
                swipeFilter.distance * METER_UNIT,
                swipeFilter.pageIndex
            )
            response.data?.let { data ->
                savePageIndex(swipeFilter.pageIndex, data.reset)
                val cardDTOs = data.cardDTOs
                for (i in cardDTOs.size - 1 downTo 0) {
                    if (swipeDAO.existBySwipedId(cardDTOs[i].accountId))
                        cardDTOs.removeAt(i)
                }
                cardDTOs.shuffle()
            }
            return@withContext response
        }
    }

    private fun yearFromAge(age: Int): Int {
        if (age == SwipeFilter.MAX_AGE) return 0
        return Calendar.getInstance().get(Calendar.YEAR) - age + 1
    }

    private suspend fun savePageIndex(currentPageIndex: Int, reset: Boolean) {
        withContext(Dispatchers.IO) {
            var pageIndex = if (reset) 0 else currentPageIndex
            pageIndex++
            swipeFilterDAO.updatePageIndex(pageIndex)
        }
    }

    companion object {
        const val METER_UNIT = 1000
    }
}