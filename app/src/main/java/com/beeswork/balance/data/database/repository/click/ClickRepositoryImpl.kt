package com.beeswork.balance.data.database.repository.click

import com.beeswork.balance.data.database.BalanceDatabase
import com.beeswork.balance.data.database.dao.ClickDAO
import com.beeswork.balance.data.database.dao.MatchDAO
import com.beeswork.balance.data.database.entity.Click
import com.beeswork.balance.data.network.rds.click.ClickRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.swipe.ClickDTO
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.data.network.service.stomp.StompClient
import com.beeswork.balance.internal.mapper.click.ClickMapper
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.threeten.bp.OffsetDateTime
import java.util.*

class ClickRepositoryImpl(
    private val clickRDS: ClickRDS,
    private val clickDAO: ClickDAO,
    private val preferenceProvider: PreferenceProvider,
    private val clickMapper: ClickMapper,
    private val matchDAO: MatchDAO,
    private val balanceDatabase: BalanceDatabase,
    private val stompClient: StompClient,
    private val scope: CoroutineScope
) : ClickRepository {

    private var newClickFlowListener: NewClickFlowListener? = null

    @ExperimentalCoroutinesApi
    override val newClickFlow: Flow<Click> = callbackFlow {
        newClickFlowListener = object : NewClickFlowListener {
            override fun onNewClickReceived(click: Click) {
                offer(click)
            }
        }
        awaitClose {  }
    }


    init {
        collectClickedFlow()
    }

    private fun collectClickedFlow() {
        stompClient.clickedFlow.onEach { clickDTO ->
            val click = clickMapper.fromDTOToEntity(clickDTO)
            if (saveClick(click)) newClickFlowListener?.onNewClickReceived(click)
        }.launchIn(scope)
    }


    override suspend fun saveClick(clickDTO: ClickDTO) {
        val click = clickMapper.fromDTOToEntity(clickDTO)
        if (saveClick(click)) newClickFlowListener?.onNewClickReceived(click)
    }

    private fun saveClick(click: Click): Boolean {
        return if (!matchDAO.existsBySwipedId(click.swiperId)) {
            clickDAO.insert(click)
            true
        } else false
    }

    override suspend fun loadClicks(loadSize: Int, startPosition: Int): List<Click> {
        return withContext(Dispatchers.IO) {
            return@withContext clickDAO.findAllPaged(loadSize, startPosition)
        }
    }

    override suspend fun fetchClicks(): Resource<EmptyResponse> {
        return withContext(Dispatchers.IO) {
            val response = clickRDS.listClicks(
                preferenceProvider.getAccountId(),
                preferenceProvider.getIdentityToken(),
                preferenceProvider.getClickFetchedAt()
            )

            response.data?.let { data ->
                val clicks = data.map { clickMapper.fromDTOToEntity(it) }
                balanceDatabase.runInTransaction {
                    clicks.forEach { click -> saveClick(click) }
                }
            }

            return@withContext response.toEmptyResponse()
        }
    }

    override fun getInvalidation(): Flow<Boolean> {
        return clickDAO.invalidation()
    }

    override fun test() {

        CoroutineScope(Dispatchers.IO).launch {
//            clickDAO.insert(Click(UUID.fromString("698f2eb6-3fef-4ee3-9c7d-3e527740548e"), "new profile", OffsetDateTime.now()))
//            clickDAO.insert(Click(UUID.fromString("cd4f05bf-1192-4f16-90c7-f97b46584ba6"), "new profile", OffsetDateTime.now()))
//            clickDAO.insert(Click(UUID.fromString("82585030-2f0e-4be5-bbf1-bbcce26d0408"), "new profile", OffsetDateTime.now()))
//            clickDAO.insert(Click(UUID.fromString("44d7c228-f670-4fe7-8302-cdfd7fdaa912"), "new profile", OffsetDateTime.now()))
//            val clicks = mutableListOf<Click>()
//            for (i in 0..1000) {
//                clicks.add(Click(UUID.randomUUID(), "test", OffsetDateTime.now()))
//            }
//            clickDAO.insert(clicks)
        }
    }


}