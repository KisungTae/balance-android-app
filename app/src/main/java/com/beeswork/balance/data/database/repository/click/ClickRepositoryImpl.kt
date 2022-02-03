package com.beeswork.balance.data.database.repository.click

import com.beeswork.balance.data.database.BalanceDatabase
import com.beeswork.balance.data.database.common.PageSyncDateTracker
import com.beeswork.balance.data.database.common.InvalidationListener
import com.beeswork.balance.data.database.dao.ClickDAO
import com.beeswork.balance.data.database.dao.MatchDAO
import com.beeswork.balance.data.database.entity.click.Click
import com.beeswork.balance.data.network.rds.click.ClickRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.click.ClickDTO
import com.beeswork.balance.data.network.response.click.FetchClicksDTO
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.internal.mapper.click.ClickMapper
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.threeten.bp.OffsetDateTime
import java.util.*

class ClickRepositoryImpl(
    private val clickRDS: ClickRDS,
    private val clickDAO: ClickDAO,
    private val matchDAO: MatchDAO,
    private val preferenceProvider: PreferenceProvider,
    private val clickMapper: ClickMapper,
    private val balanceDatabase: BalanceDatabase,
    private val ioDispatcher: CoroutineDispatcher
) : ClickRepository {

    private var newClickInvalidationListener: InvalidationListener<Click>? = null
    private var clickCountInvalidationListener: InvalidationListener<Long?>? = null
    private var clickPageInvalidationListener: InvalidationListener<Boolean>? = null
    private val clickPageSyncDateTracker = PageSyncDateTracker()

    @ExperimentalCoroutinesApi
    override val newClickInvalidationFlow: Flow<Click> = callbackFlow {
        newClickInvalidationListener = object : InvalidationListener<Click> {
            override fun onInvalidate(data: Click) {
                offer(data)
            }
        }
        awaitClose { }
    }

    @ExperimentalCoroutinesApi
    override val clickCountInvalidationFlow: Flow<Long?> = callbackFlow {
        clickCountInvalidationListener = object : InvalidationListener<Long?> {
            override fun onInvalidate(data: Long?) {
                offer(data)
            }
        }
        awaitClose { }
    }

    @ExperimentalCoroutinesApi
    override val clickPageInvalidationFlow: Flow<Boolean> = callbackFlow {
        clickPageInvalidationListener = object : InvalidationListener<Boolean> {
            override fun onInvalidate(data: Boolean) {
                offer(data)
            }
        }
        awaitClose { }
    }



    override suspend fun fetchClicks(loadSize: Int, lastSwiperId: UUID?): Resource<FetchClicksDTO> {
        return withContext(ioDispatcher) {
            val response = clickRDS.fetchClicks(loadSize, lastSwiperId)
            if (response.isError()) {
                return@withContext Resource.error(response.exception)
            }

            balanceDatabase.runInTransaction {
                response.data?.forEach { clickDTO ->
                    doSaveClick(clickDTO)
                }
            }
            val fetchedClickSize = response.data?.size ?: 0
            return@withContext Resource.success(FetchClicksDTO(fetchedClickSize))
        }
    }

    override suspend fun loadClicks(loadSize: Int, startPosition: Int): List<Click> {
        return withContext(ioDispatcher) {
            if (clickPageSyncDateTracker.shouldSyncPage(startPosition)) {
                syncClicks(loadSize, startPosition)
            }
            return@withContext clickDAO.findAllPaged(preferenceProvider.getAccountId(), loadSize, startPosition)
        }
    }

    private fun syncClicks(loadSize: Int, startPosition: Int) {
        CoroutineScope(ioDispatcher).launch(CoroutineExceptionHandler { a, b -> }) {
            clickPageSyncDateTracker.updateSyncDate(startPosition, OffsetDateTime.now())
            val response = clickRDS.listClicks(loadSize, startPosition)
            if (response.isError()) {
                clickPageSyncDateTracker.updateSyncDate(startPosition, null)
                return@launch
            }
            balanceDatabase.runInTransaction {
                response.data?.forEach { clickDTO ->
                    if (clickDTO.deleted) {
                        clickDAO.deleteBy(clickDTO.swiperId)
                    } else {
                        doSaveClick(clickDTO)
                    }
                }
            }
        }
    }

    override suspend fun saveClick(clickDTO: ClickDTO) {
        withContext(Dispatchers.IO) {
            doSaveClick(clickDTO)?.let { click ->
                if (click.swipedId == preferenceProvider.getAccountId()) {
                    newClickInvalidationListener?.onInvalidate(click)
                }
            }
        }
    }

    private fun doSaveClick(clickDTO: ClickDTO): Click? {
        if (matchDAO.existBy(clickDTO.swipedId, clickDTO.swiperId)) {
            return null
        }

        val oldClick = clickDAO.findBy(clickDTO.swiperId, clickDTO.swipedId)
        if (oldClick == null || !oldClick.isEqualTo(clickDTO)) {
            clickMapper.toClick(clickDTO)?.let { click ->
                clickDAO.insert(click)
                return click
            }
        }
        return null
    }

    override suspend fun syncClickCount() {
        withContext(ioDispatcher) {
            val response = clickRDS.countClicks()
            clickCountInvalidationListener?.onInvalidate(response.data?.count)
        }
    }

    override suspend fun deleteClicks() {
        withContext(ioDispatcher) {
            clickDAO.deleteAll(preferenceProvider.getAccountId())
        }
    }

    override fun test() {
        println("test()")
        clickPageInvalidationListener?.onInvalidate(true)
//        CoroutineScope(ioDispatcher).launch {
//            val clicks = mutableListOf<Click>()
//            val accountId = preferenceProvider.getAccountId()
//            var now = OffsetDateTime.now()
//            for (i in 0..500) {
//                now = now.plusMinutes(1)
//                clicks.add(Click(0, UUID.randomUUID(), accountId!!, "test-$i", false, "photo"))
//            }
//            clickDAO.insert(clicks)
//            println("inserted click")
//            clickPageInvalidationListener?.onInvalidate(null)
//            println("called clickPageInvalidationListener?.onInvalidate(null)")
//        }
    }


}