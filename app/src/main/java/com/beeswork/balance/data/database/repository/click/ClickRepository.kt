package com.beeswork.balance.data.database.repository.click

import com.beeswork.balance.data.network.response.swipe.SwipeDTO

interface ClickRepository {
    suspend fun saveClick(swipeDTO: SwipeDTO)
}