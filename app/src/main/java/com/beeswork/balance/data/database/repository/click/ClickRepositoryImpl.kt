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
    override val newClickFlow: Flow<Click> = callbackFlow {
        newClickInvalidationListener = object : InvalidationListener<Click> {
            override fun onInvalidate(data: Click) {
                offer(data)
            }
        }
        awaitClose { }
    }

    @ExperimentalCoroutinesApi
    override val clickCountFlow: Flow<Long?> = callbackFlow {
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
            var insertedClickCount = 0
            balanceDatabase.runInTransaction {
                response.data?.forEach { clickDTO ->
                    if (insertClick(clickDTO) != null) {
                        insertedClickCount++
                    }
                }
            }
            if (insertedClickCount > 0) {
                clickPageInvalidationListener?.onInvalidate(true)
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
            var deletedClickCount = 0
            var insertedClickCount = 0
            balanceDatabase.runInTransaction {
                response.data?.let { listClicksDTO ->
                    deletedClickCount += clickDAO.deleteIn(listClicksDTO.deletedSwiperIds)
                    listClicksDTO.clickDTOs.forEach { clickDTO ->
                        if (insertClick(clickDTO) != null) {
                            insertedClickCount++;
                        }
                    }
                }
            }
            if (deletedClickCount > 0 || insertedClickCount > 0) {
                clickPageInvalidationListener?.onInvalidate(true)
            }
        }
    }

    override suspend fun saveClick(clickDTO: ClickDTO) {
        withContext(Dispatchers.IO) {
            balanceDatabase.runInTransaction {
                insertClick(clickDTO)?.let { click ->
                    if (click.swipedId == preferenceProvider.getAccountId()) {
                        clickPageInvalidationListener?.onInvalidate(true)
                        newClickInvalidationListener?.onInvalidate(click)
                    }
                }
            }
        }
    }

    private fun isClickInsertable(clickDTO: ClickDTO): Boolean {
        if (clickDTO.deleted) {
            return false
        }
        if (matchDAO.existBy(clickDTO.swipedId, clickDTO.swiperId)) {
            return false
        }
        val oldClick = clickDAO.findBy(clickDTO.swiperId, clickDTO.swipedId)
        return oldClick == null || !oldClick.isEqualTo(clickDTO)
    }

    private fun insertClick(clickDTO: ClickDTO): Click? {
        if (isClickInsertable(clickDTO)) {
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
        CoroutineScope(ioDispatcher).launch {
            val click = Click(1, UUID.randomUUID(), UUID.randomUUID(), "Michael", false, "profiel photo");
            newClickInvalidationListener?.onInvalidate(click)
        }

//        CoroutineScope(ioDispatcher).launch {
//            val ids = mutableListOf<UUID>()
//            ids.add(UUID.fromString("6e6a7f07-0d1f-435e-9e48-07ab038ebc8b"))
//            ids.add(UUID.fromString("a5abff5e-df06-47e8-9b33-d5fe85d2fb86"))
//
//            println("deleted: ${clickDAO.deleteIn(ids)}")
//        }


//        1	1366	6e6a7f07-0d1f-435e-9e48-07ab038ebc8b	2c2743bf-23ab-4e23-bd4e-4955b8191e12	user-201	1	1ca9c1b6-b1c3-4ed8-9735-c6fbdd7c5cd4.jpg
//        2	1367	a5abff5e-df06-47e8-9b33-d5fe85d2fb86	2c2743bf-23ab-4e23-bd4e-4955b8191e12	user-202	0	b092c2b4-316b-4bfc-b8d4-1387e1a6237d.jpg
//        CoroutineScope(ioDispatcher).launch {
//            val uuid = UUID.fromString("255f2a47-91e4-44a0-bcc0-18f6bc2018ac")
//            println("clickDAO.deleteBy(UUID.randomUUID()): ${clickDAO.deleteBy(uuid)}")
//        }


//        clickPageInvalidationListener?.onInvalidate(true)
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