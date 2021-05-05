package com.beeswork.balance.data.database.repository.click

import com.beeswork.balance.data.database.dao.ClickDAO
import com.beeswork.balance.data.network.rds.click.ClickRDS
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.service.stomp.StompClient
import kotlinx.coroutines.flow.onEach

class ClickRepositoryImpl(
    private val clickRDS: ClickRDS,
    private val clickDAO: ClickDAO,
    private val preferenceProvider: PreferenceProvider,
    private val stompClient: StompClient,
) : ClickRepository {

    init {
        collectClickedFlow()
    }

    private fun collectClickedFlow() {
        stompClient.clickedFlow.onEach { swipeDTO ->

        }
    }
}