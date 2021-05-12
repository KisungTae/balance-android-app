package com.beeswork.balance.data.database.repository.swipe

import com.beeswork.balance.data.database.dao.SwipeFilterDAO
import com.beeswork.balance.data.network.rds.swipe.SwipeRDS
import com.beeswork.balance.internal.provider.preference.PreferenceProvider

class SwipeRepositoryImpl(
    private val preferenceProvider: PreferenceProvider,
    private val swipeFilterDAO: SwipeFilterDAO,
    private val swipeRDS: SwipeRDS
): SwipeRepository {
}