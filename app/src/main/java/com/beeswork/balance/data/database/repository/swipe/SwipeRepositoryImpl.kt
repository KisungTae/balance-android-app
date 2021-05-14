package com.beeswork.balance.data.database.repository.swipe

import com.beeswork.balance.data.database.dao.SwipeFilterDAO
import com.beeswork.balance.data.database.entity.SwipeFilter
import com.beeswork.balance.data.network.rds.swipe.SwipeRDS
import com.beeswork.balance.internal.constant.Gender
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SwipeRepositoryImpl(
    private val preferenceProvider: PreferenceProvider,
    private val swipeFilterDAO: SwipeFilterDAO,
    private val swipeRDS: SwipeRDS
): SwipeRepository {

    override suspend fun getSwipeFilter(): SwipeFilter {
        return withContext(Dispatchers.IO) {
            return@withContext swipeFilterDAO.findById()
        }
    }

    override suspend fun saveSwipeFilter(gender: Gender, minAge: Int, maxAge: Int, distance: Int) {
        withContext(Dispatchers.IO) {
            swipeFilterDAO.insert(SwipeFilter(gender, minAge, maxAge, distance))
        }
    }
}