package com.beeswork.balance.data.database.repository.click

import com.beeswork.balance.data.database.dao.ClickDAO
import com.beeswork.balance.data.database.entity.Click
import com.beeswork.balance.data.network.rds.click.ClickRDS
import com.beeswork.balance.data.network.response.swipe.SwipeDTO
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.data.network.service.stomp.StompClient
import com.beeswork.balance.internal.mapper.click.ClickMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ClickRepositoryImpl(
    private val clickRDS: ClickRDS,
    private val clickDAO: ClickDAO,
    private val preferenceProvider: PreferenceProvider,
    private val clickMapper: ClickMapper,
    private val stompClient: StompClient,
    private val scope: CoroutineScope
) : ClickRepository {

    init {
        collectClickedFlow()
    }

    private fun collectClickedFlow() {
        stompClient.clickedFlow.onEach { swipeDTO ->

        }.launchIn(scope)
    }

    override suspend fun saveClick(swipeDTO: SwipeDTO) {
        TODO("Not yet implemented")
    }

    private fun saveClick(click: Click) {

    }
}