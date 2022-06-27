package com.beeswork.balance.data.database.repository.swipe

import com.beeswork.balance.data.database.BalanceDatabase
import com.beeswork.balance.data.database.common.CallBackFlowListener
import com.beeswork.balance.data.database.dao.SwipeDAO
import com.beeswork.balance.data.database.entity.swipe.Swipe
import com.beeswork.balance.data.database.repository.tabcount.TabCountRepository
import com.beeswork.balance.data.network.rds.swipe.SwipeRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.swipe.SwipeDTO
import com.beeswork.balance.data.network.response.swipe.ListSwipesDTO
import com.beeswork.balance.data.network.service.stomp.StompClient
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.internal.mapper.swipe.SwipeMapper
import com.beeswork.balance.ui.common.paging.LoadType
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.threeten.bp.OffsetDateTime
import java.util.*
import java.util.concurrent.Callable

@ExperimentalCoroutinesApi
class SwipeRepositoryImpl(
    private val swipeRDS: SwipeRDS,
    private val swipeDAO: SwipeDAO,
    private val tabCountRepository: TabCountRepository,
    private val preferenceProvider: PreferenceProvider,
    private val swipeMapper: SwipeMapper,
    private val stompClient: StompClient,
    private val applicationScope: CoroutineScope,
    private val balanceDatabase: BalanceDatabase,
    private val ioDispatcher: CoroutineDispatcher
) : SwipeRepository {

    private var newSwipeCallBackFlowListener: CallBackFlowListener<Swipe>? = null

    @ExperimentalCoroutinesApi
    override val newSwipeFlow: Flow<Swipe> = callbackFlow {
        newSwipeCallBackFlowListener = object : CallBackFlowListener<Swipe> {
            override fun onInvoke(data: Swipe) {
                offer(data)
            }
        }
        awaitClose { }
    }

    override suspend fun loadSwipes(key: Long?, loadType: LoadType, loadSize: Int): Resource<List<Swipe>> {
        TODO("Not yet implemented")
    }

    private var refreshedPagesSyncedAt = OffsetDateTime.MIN

    init {
        stompClient.swipeFlow.onEach { swipeDTO ->
            saveSwipe(swipeDTO)
        }.launchIn(applicationScope)
    }

    override suspend fun fetchSwipes(loadSize: Int, lastSwipeId: Long?): Resource<ListSwipesDTO> {
        return withContext(ioDispatcher) {
            val response = swipeRDS.fetchSwipes(loadSize, lastSwipeId)
//            if (response.isError()) {
//                return@withContext Resource.error(response.exception)
//            }
//            balanceDatabase.runInTransaction {
//                response.data?.let { listSwipesDTO ->
//                    saveSwipes(listSwipesDTO)
//                }
//            }
            return@withContext response
        }
    }

    private fun listSwipes(loadSize: Int, startPosition: Int) {
//        applicationScope.launch(CoroutineExceptionHandler { _, _ -> }) {
//            val response = swipeRDS.listSwipes(loadSize, startPosition)
//            balanceDatabase.runInTransaction {
//                response.data?.let { listSwipesDTO ->
//                    saveSwipes(listSwipesDTO)
//                }
//            }
//        }
    }

    private fun saveSwipes(listSwipesDTO: ListSwipesDTO) {
//        listSwipesDTO.swipeDTOs.forEach { swipeDTO ->
//            if (swipeDTO.swiperDeleted) {
//                swipeDAO.deleteBy(swipeDTO.swiperId, swipeDTO.swipedId)
//            } else {
//                swipeMapper.toSwipe(swipeDTO)?.let { swipe ->
//                    swipeDAO.insert(swipe)
//                }
//            }
//        }
//        updateSwipeCount(listSwipesDTO.swipeCount, listSwipesDTO.swipeCountCountedAt)
    }

    private fun updateSwipeCount(count: Long, countedAt: OffsetDateTime) {
//        val accountId = preferenceProvider.getAccountId() ?: return
//        val swipeCount = swipeCountDAO.getBy(accountId)
//        if (swipeCount == null) {
//            swipeCountDAO.insert(SwipeCount(accountId, count, countedAt))
//        } else {
//            if (swipeCount.countedAt.isBefore(countedAt)) {
//                swipeCount.count = count
//                swipeCount.countedAt = countedAt
//                swipeCountDAO.insert(swipeCount)
//            }
//        }
    }

    private fun incrementSwipeCount(swipeDTO: SwipeDTO) {
//        val swipeCount = swipeCountDAO.getBy(swipeDTO.swipedId)
//        if (swipeCount == null) {
//            swipeCountDAO.insert(SwipeCount(swipeDTO.swipedId!!, 1))
//        } else {
//            if (swipeDTO.updatedAt?.isAfter(swipeCount.countedAt) == true) {
//                swipeCount.count = swipeCount.count + 1
//                swipeCountDAO.insert(swipeCount)
//            }
//        }
    }

    override suspend fun loadSwipes(loadSize: Int, startPosition: Int, sync: Boolean): List<Swipe> {
        return withContext(ioDispatcher) {
//            if (sync) {
//                listSwipes(loadSize, startPosition)
//            }
            return@withContext swipeDAO.getAllPagedBy(preferenceProvider.getAccountId(), loadSize, startPosition)
        }
    }

    override suspend fun saveSwipe(swipeDTO: SwipeDTO) {
        withContext(Dispatchers.IO) {
            val newSwipe = balanceDatabase.runInTransaction(Callable {
                val exists = swipeDAO.existsBy(swipeDTO.swiperId, swipeDTO.swipedId)
                val swipe = swipeMapper.toSwipe(swipeDTO)
                if (swipe != null) {
                    swipeDAO.insert(swipe)
                }

                if (exists) {
                    return@Callable null
                } else {
                    incrementSwipeCount(swipeDTO)
                    return@Callable swipe
                }
            })
            if (newSwipe != null && newSwipe.swipedId == preferenceProvider.getAccountId()) {
                newSwipeCallBackFlowListener?.onInvoke(newSwipe)
            }
        }
    }

    override suspend fun deleteSwipes() {
        withContext(ioDispatcher) {
            swipeDAO.deleteAllBy(preferenceProvider.getAccountId())
        }
    }

    override fun getSwipePageInvalidationFlow(): Flow<Boolean> {
        return swipeDAO.getPageInvalidationFlow()
    }

    override fun syncSwipes(loadSize: Int, startPosition: Int?) {
        if (loadSize <= 0 || startPosition == null) {
            return
        }

        stompClient.getStompReconnectedAt()?.let { stompReconnectedAt ->
            if (stompReconnectedAt.isAfter(refreshedPagesSyncedAt)) {
                listSwipes(loadSize, startPosition)
                refreshedPagesSyncedAt = stompReconnectedAt
            }
        }
    }

    override suspend fun test() {
        withContext(ioDispatcher) {

//            for (i in 0..99) {
//                swipeDAO.insert(Swipe(Random().nextLong(), preferenceProvider.getAccountId()!!, UUID.randomUUID(), false, null))
//            }

//            swipeCountDAO.insert(SwipeCount(preferenceProvider.getAccountId()!!, Random.nextInt(10)))

//            saveSwipe(SwipeDTO(Random.nextLong(), UUID.randomUUID(), preferenceProvider.getAccountId()!!, false, null, false, ""))


        }
    }


}