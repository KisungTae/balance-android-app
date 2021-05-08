package com.beeswork.balance.data.database.repository.click

import com.beeswork.balance.data.database.dao.ClickDAO
import com.beeswork.balance.data.database.entity.Click
import com.beeswork.balance.data.network.rds.click.ClickRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.swipe.ClickDTO
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.data.network.service.stomp.StompClient
import com.beeswork.balance.internal.mapper.click.ClickMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.OffsetDateTime
import java.util.*

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

    override suspend fun saveClick(clickDTO: ClickDTO) {
        TODO("Not yet implemented")
    }

    override suspend fun loadClicks(loadSize: Int, startPosition: Int): List<Click> {
        return withContext(Dispatchers.IO) {
            return@withContext clickDAO.findAllPaged(loadSize, startPosition)
        }
    }

    override suspend fun fetchClicks(): Resource<EmptyResponse> {
        TODO("Not yet implemented")
    }

    override fun initInvalidation(): Flow<Boolean> {
        return clickDAO.changed()
    }

    override fun test() {
        CoroutineScope(Dispatchers.IO).launch {
            val clicks = mutableListOf<Click>()
            for (i in 0..1000) {
                clicks.add(Click(UUID.randomUUID(), "test", OffsetDateTime.now()))
            }
            clickDAO.insert(clicks)
        }
    }

    private fun saveClick(click: Click) {

    }
}