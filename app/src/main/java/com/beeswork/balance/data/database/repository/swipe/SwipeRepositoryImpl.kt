package com.beeswork.balance.data.database.repository.swipe

import com.beeswork.balance.data.database.BalanceDatabase
import com.beeswork.balance.data.database.common.PageFetchDateTracker
import com.beeswork.balance.data.database.common.InvalidationListener
import com.beeswork.balance.data.database.common.QueryResult
import com.beeswork.balance.data.database.dao.SwipeDAO
import com.beeswork.balance.data.database.dao.MatchDAO
import com.beeswork.balance.data.database.dao.SwipeCountDAO
import com.beeswork.balance.data.database.entity.swipe.Swipe
import com.beeswork.balance.data.database.entity.swipe.SwipeCount
import com.beeswork.balance.data.network.rds.swipe.SwipeRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.swipe.SwipeDTO
import com.beeswork.balance.data.network.response.swipe.ListSwipesDTO
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.internal.mapper.swipe.SwipeMapper
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.threeten.bp.OffsetDateTime
import java.util.*
import java.util.concurrent.Callable

class SwipeRepositoryImpl(
    private val swipeRDS: SwipeRDS,
    private val swipeDAO: SwipeDAO,
    private val matchDAO: MatchDAO,
    private val swipeCountDAO: SwipeCountDAO,
    private val preferenceProvider: PreferenceProvider,
    private val swipeMapper: SwipeMapper,
    private val balanceDatabase: BalanceDatabase,
    private val ioDispatcher: CoroutineDispatcher
) : SwipeRepository {

    private lateinit var newSwipeInvalidationListener: InvalidationListener<Swipe>
    private val swipePageFetchDateTracker = PageFetchDateTracker(5L)

    @ExperimentalCoroutinesApi
    override val newSwipeFlow: Flow<Swipe> = callbackFlow {
        newSwipeInvalidationListener = object : InvalidationListener<Swipe> {
            override fun onInvalidate(data: Swipe) {
                offer(data)
            }
        }
        awaitClose { }
    }

    override suspend fun fetchSwipes(loadSize: Int, lastSwipeId: Long?): Resource<ListSwipesDTO> {
        return withContext(ioDispatcher) {
            val response = swipeRDS.fetchSwipes(loadSize, lastSwipeId)
            if (response.isError()) {
                return@withContext Resource.error(response.exception)
            }
            balanceDatabase.runInTransaction {
                response.data?.let { listSwipesDTO ->
                    saveSwipes(listSwipesDTO)
                }
            }
            return@withContext response
        }
    }

    private fun listSwipes(loadSize: Int, startPosition: Int) {
        CoroutineScope(ioDispatcher).launch(CoroutineExceptionHandler { _, _ -> }) {
            swipePageFetchDateTracker.updateFetchDate(startPosition, OffsetDateTime.now())
            val response = swipeRDS.listSwipes(loadSize, startPosition)
            if (response.isError()) {
                swipePageFetchDateTracker.updateFetchDate(startPosition, null)
                return@launch
            }
            balanceDatabase.runInTransaction {
                response.data?.let { listSwipesDTO ->
                    saveSwipes(listSwipesDTO)
                }
            }
        }
    }

    private fun saveSwipes(listSwipesDTO: ListSwipesDTO) {
        listSwipesDTO.swipeDTOs.forEach { swipeDTO ->
            if (swipeDTO.swiperDeleted) {
                swipeDAO.deleteBy(swipeDTO.swiperId, swipeDTO.swipedId)
            } else {
                insertSwipe(swipeDTO)
            }
        }
        updateSwipeCount(listSwipesDTO.swipeCount, listSwipesDTO.swipeCountCountedAt)
    }

    private fun insertSwipe(swipeDTO: SwipeDTO): QueryResult<Swipe> {
        if (swipeDTO.swiperDeleted || matchDAO.existBy(swipeDTO.swipedId, swipeDTO.swiperId)) {
            return QueryResult.none()
        }
        val swipe = swipeDAO.getBy(swipeDTO.swiperId, swipeDTO.swipedId)
        if (swipe == null || !swipe.isEqualTo(swipeDTO)) {
            val newSwipe = swipeMapper.toSwipe(swipeDTO) ?: return QueryResult.none()
            swipeDAO.insert(newSwipe)
            return if (swipe == null) {
                QueryResult.insert(newSwipe)
            } else {
                QueryResult.update(newSwipe)
            }
        }
        return QueryResult.none()
    }

    private fun updateSwipeCount(count: Long, countedAt: OffsetDateTime) {
        val accountId = preferenceProvider.getAccountId() ?: return
        val swipeCount = swipeCountDAO.getBy(accountId)
        if (swipeCount == null) {
            swipeCountDAO.insert(SwipeCount(accountId, count, countedAt))
        } else {
            if (swipeCount.countedAt.isBefore(countedAt)) {
                swipeCount.count = count
                swipeCount.countedAt = countedAt
                swipeCountDAO.insert(swipeCount)
            }
        }
    }

    private fun incrementSwipeCount(swipeDTO: SwipeDTO) {
        val swipeCount = swipeCountDAO.getBy(swipeDTO.swipedId)
        if (swipeCount == null) {
            swipeCountDAO.insert(SwipeCount(swipeDTO.swipedId!!, 1))
        } else {
            if (swipeDTO.updatedAt?.isAfter(swipeCount.countedAt) == true) {
                swipeCount.count = swipeCount.count + 1
                swipeCountDAO.insert(swipeCount)
            }
        }
    }

    override suspend fun loadSwipes(loadSize: Int, startPosition: Int): List<Swipe> {
        return withContext(ioDispatcher) {
            if (swipePageFetchDateTracker.shouldFetchPage(startPosition)) {
                listSwipes(loadSize, startPosition)
            }
            return@withContext swipeDAO.getAllPagedBy(preferenceProvider.getAccountId(), loadSize, startPosition)
        }
    }

    override suspend fun saveSwipe(swipeDTO: SwipeDTO) {
        withContext(Dispatchers.IO) {
            val swipe = balanceDatabase.runInTransaction(Callable {
                val queryResult = insertSwipe(swipeDTO)
                if (queryResult.isInsert()) {
                    incrementSwipeCount(swipeDTO)
                }
                return@Callable queryResult.data
            })
            if (swipe != null && swipe.swipedId == preferenceProvider.getAccountId()) {
                newSwipeInvalidationListener.onInvalidate(swipe)
            }
        }
    }

    override suspend fun deleteSwipes() {
        withContext(ioDispatcher) {
            swipeDAO.deleteAllBy(preferenceProvider.getAccountId())
        }
    }

    override suspend fun deleteSwipeSwipeCount() {
        withContext(ioDispatcher) {
            swipeCountDAO.deleteBy(preferenceProvider.getAccountId())
        }
    }

    override fun getSwipePageInvalidationFlow(): Flow<Boolean> {
        return swipeDAO.getPageInvalidationFlow()
    }

    override fun getSwipeCountFlow(): Flow<Long?> {
        return swipeCountDAO.getCountFlowBy(preferenceProvider.getAccountId())
    }

    override fun test() {

    }


}