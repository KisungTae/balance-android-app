package com.beeswork.balance.data.database.repository.click

import com.beeswork.balance.data.database.BalanceDatabase
import com.beeswork.balance.data.database.common.PageFetchDateTracker
import com.beeswork.balance.data.database.common.PageInvalidationListener
import com.beeswork.balance.data.database.dao.ClickDAO
import com.beeswork.balance.data.database.dao.MatchDAO
import com.beeswork.balance.data.database.entity.click.Click
import com.beeswork.balance.data.network.rds.click.ClickRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.click.ClickDTO
import com.beeswork.balance.data.network.response.common.EmptyResponse
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

    private var clickPageInvalidationListener: PageInvalidationListener<Click?>? = null
    private val clickPageFetchDateTracker = PageFetchDateTracker()

    @ExperimentalCoroutinesApi
    override val clickPageInvalidationFlow: Flow<Click?> = callbackFlow {
        clickPageInvalidationListener = object : PageInvalidationListener<Click?> {
            override fun onInvalidate(data: Click?) {
                offer(data)
            }
        }
        awaitClose { }
    }

    override suspend fun deleteClicks() {
        withContext(ioDispatcher) {
            clickDAO.deleteAll(preferenceProvider.getAccountId())
            clickPageInvalidationListener?.onInvalidate(null)
        }
    }

    override suspend fun saveClick(clickDTO: ClickDTO) {
        withContext(Dispatchers.IO) {
            if (!matchDAO.existBySwipedId(clickDTO.swipedId, clickDTO.swiperId)) {
                val click = clickMapper.toClick(clickDTO)
                if (click != null) {
                    clickDAO.insert(click)
                    if (click.swipedId == preferenceProvider.getAccountId()) {
                        clickPageInvalidationListener?.onInvalidate(click)
                    }
                }
            }
        }
    }

//    todo: remove me
    var count = 0

    override suspend fun loadClicks(loadSize: Int, startPosition: Int): Resource<List<Click>> {
        return withContext(ioDispatcher) {
            var clicks = clickDAO.findAllPaged(preferenceProvider.getAccountId(), loadSize, startPosition)
//            count++
//            if (count == 10) {
//                delay(60000)
//                println("end of delay(60000)")
//            }
//            if (clicks.isEmpty()) {
//                val response = fetchClicks(loadSize, startPosition)
//                if (response.isError())
//
//
//                val response = clickRDS.listClicks(loadSize, startPosition)
//                if (response.isError()) {
//                    return@withContext Resource.error(response.exception)
//                }
//
//                val newClicks = mutableListOf<Click>()
//                val deletedClicks = mutableListOf<Click>()
//                response.data?.forEach { clickDTO ->
//                    newClicks.add(clickMapper.toClick(clickDTO))
//                }
//                clickDAO.insert(newClicks)
//                clicks = clickDAO.findAllPaged(preferenceProvider.getAccountId(), loadSize, startPosition)
//            } else {
//
//            }
            return@withContext Resource.success(clicks)
        }
    }

    override suspend fun fetchClicks(loadSize: Int, lastItemId: UUID): Resource<Int> {
        return withContext(ioDispatcher) {
            val response = clickRDS.fetchClicks(loadSize, lastItemId)

            balanceDatabase.runInTransaction {
                response.data?.forEach { clickDTO ->
                    if (!matchDAO.existBySwipedId(clickDTO.swipedId, clickDTO.swiperId)) {
                        val click = clickMapper.toClick(clickDTO)
                        if (click != null) {
                            clickDAO.insert(click)
                        }
                    }
                }
            }



            return@withContext Resource.error(null)
        }
    }

    private suspend fun fetchClicks(loadSize: Int, startPosition: Int): Resource<EmptyResponse> {
        val response = clickRDS.listClicks(loadSize, startPosition)
        if (response.isError()) {
            return response.toEmptyResponse()
        }
        balanceDatabase.runInTransaction {
            response.data?.forEach { clickDTO ->
                if (clickDTO.deleted) {
                    clickDAO.deleteBySwiperId(clickDTO.swiperId)
                } else if (matchDAO.existBySwipedId(clickDTO.swipedId, clickDTO.swiperId)) {
                    clickDAO.deleteBySwiperId(clickDTO.swiperId, clickDTO.swipedId)
                } else {
                    val newClick = clickMapper.toClick(clickDTO)

//                    if (click != null) {
//                        clickDAO.insert(click)
//                    }
                }
            }
        }
        return response.toEmptyResponse()
    }

//    override suspend fun fetchClicks(): Resource<EmptyResponse> {
//        return withContext(ioDispatcher) {
//            val accountId = preferenceProvider.getAccountId()
//                ?: return@withContext Resource.error(ExceptionCode.ACCOUNT_ID_NOT_FOUND_EXCEPTION)
//
//            val response = clickRDS.listClicks(fetchInfoDAO.findClickFetchedAt(accountId),)
//
//            response.data?.let { data ->
//                var clickFetchedAt = OffsetDateTime.MIN
//                balanceDatabase.runInTransaction {
//                    data.forEach { clickDTO ->
//                        if (clickDTO.deleted)
//                            clickDAO.deleteBySwiperId(accountId, clickDTO.swiperId)
//                        else if (!matchDAO.existBySwipedId(accountId, clickDTO.swiperId)) {
//                            val click = clickMapper.toClick(clickDTO)
//                            click.swipedId = accountId
//                            clickDAO.insert(click)
//                        }
//                        if (clickDTO.updatedAt.isAfter(clickFetchedAt))
//                            clickFetchedAt = clickDTO.updatedAt
//                    }
//                }
//                if (clickFetchedAt.isAfter(OffsetDateTime.MIN))
//                    fetchInfoDAO.updateClickFetchedAt(accountId, clickFetchedAt)
//            }
//            return@withContext response.toEmptyResponse()
//            return@withContext Resource.success(EmptyResponse())
//        }
//    }

//    override fun getClickInvalidationFlow(): Flow<Boolean> {
//        return clickDAO.invalidation()
//    }

    override fun getClickCountFlow(): Flow<Int> {
        return clickDAO.count(preferenceProvider.getAccountId())
    }


    override fun test() {

        CoroutineScope(ioDispatcher).launch {
            val clicks = mutableListOf<Click>()
            val accountId = preferenceProvider.getAccountId()
            var now = OffsetDateTime.now()
            for (i in 0..500) {
                now = now.plusMinutes(1)
                clicks.add(Click(UUID.randomUUID(), accountId!!, "test-$i", "photo", OffsetDateTime.from(now)))
            }
            clickDAO.insert(clicks)
//            println("inserted click")
//            clickPageInvalidationListener?.onInvalidate(null)
//            println("called clickPageInvalidationListener?.onInvalidate(null)")
        }
    }


}