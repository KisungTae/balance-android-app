package com.beeswork.balance.data.database.repository.swipe

import com.beeswork.balance.data.database.BalanceDatabase
import com.beeswork.balance.data.database.common.PageSyncDateTracker
import com.beeswork.balance.data.database.common.InvalidationListener
import com.beeswork.balance.data.database.dao.SwipeDAO
import com.beeswork.balance.data.database.dao.MatchDAO
import com.beeswork.balance.data.database.entity.swipe.Swipe
import com.beeswork.balance.data.network.rds.swipe.SwipeRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.swipe.SwipeDTO
import com.beeswork.balance.data.network.response.swipe.FetchSwipesDTO
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.internal.mapper.swipe.SwipeMapper
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.threeten.bp.OffsetDateTime
import java.util.*

class SwipeRepositoryImpl(
    private val swipeRDS: SwipeRDS,
    private val swipeDAO: SwipeDAO,
    private val matchDAO: MatchDAO,
    private val preferenceProvider: PreferenceProvider,
    private val swipeMapper: SwipeMapper,
    private val balanceDatabase: BalanceDatabase,
    private val ioDispatcher: CoroutineDispatcher
) : SwipeRepository {

    private var newSwipeInvalidationListener: InvalidationListener<Swipe>? = null
    private var swipeCountInvalidationListener: InvalidationListener<Long?>? = null
    private val swipePageSyncDateTracker = PageSyncDateTracker()

    @ExperimentalCoroutinesApi
    override val newSwipeFlow: Flow<Swipe> = callbackFlow {
        newSwipeInvalidationListener = object : InvalidationListener<Swipe> {
            override fun onInvalidate(data: Swipe) {
                offer(data)
            }
        }
        awaitClose { }
    }

    @ExperimentalCoroutinesApi
    override val swipeCountFlow: Flow<Long?> = callbackFlow {
        swipeCountInvalidationListener = object : InvalidationListener<Long?> {
            override fun onInvalidate(data: Long?) {
                offer(data)
            }
        }
        awaitClose { }
    }

    override suspend fun fetchSwipes(loadSize: Int, lastSwiperId: UUID?): Resource<FetchSwipesDTO> {
        return withContext(ioDispatcher) {
            val response = swipeRDS.fetchSwipes(loadSize, lastSwiperId)
            if (response.isError()) {
                return@withContext Resource.error(response.exception)
            }
            balanceDatabase.runInTransaction {
                response.data?.forEach { swipeDTO ->
                    insertSwipe(swipeDTO)
                }
            }
            val fetchedSwipeSize = response.data?.size ?: 0
            return@withContext Resource.success(FetchSwipesDTO(fetchedSwipeSize))
        }
    }

    override suspend fun loadSwipes(loadSize: Int, startPosition: Int): List<Swipe> {
        return withContext(ioDispatcher) {
            if (swipePageSyncDateTracker.shouldSyncPage(startPosition)) {
                syncSwipes(loadSize, startPosition)
            }
            return@withContext swipeDAO.findAllPaged(preferenceProvider.getAccountId(), loadSize, startPosition)
        }
    }

    private fun syncSwipes(loadSize: Int, startPosition: Int) {
        CoroutineScope(ioDispatcher).launch(CoroutineExceptionHandler { a, b -> }) {
            swipePageSyncDateTracker.updateSyncDate(startPosition, OffsetDateTime.now())
            val response = swipeRDS.listSwipes(loadSize, startPosition)
            if (response.isError()) {
                swipePageSyncDateTracker.updateSyncDate(startPosition, null)
                return@launch
            }
            balanceDatabase.runInTransaction {
                response.data?.forEach { swipeDTO ->
                    if (swipeDTO.swiperDeleted) {
                        swipeDAO.deleteBy(swipeDTO.swiperId)
                    } else {
                        insertSwipe(swipeDTO)
                    }
                }
            }
        }
    }

    override suspend fun saveSwipe(swipeDTO: SwipeDTO) {
        withContext(Dispatchers.IO) {
            balanceDatabase.runInTransaction {
                insertSwipe(swipeDTO)?.let { swipe ->
                    if (swipe.swipedId == preferenceProvider.getAccountId()) {
                        newSwipeInvalidationListener?.onInvalidate(swipe)
                    }
                }
            }
        }
    }

    private fun isSwipeInsertable(swipeDTO: SwipeDTO): Boolean {
        if (swipeDTO.swiperDeleted) {
            return false
        }
        if (matchDAO.existBy(swipeDTO.swipedId, swipeDTO.swiperId)) {
            return false
        }
        val oldSwipe = swipeDAO.findBy(swipeDTO.swiperId, swipeDTO.swipedId)
        return oldSwipe == null || !oldSwipe.isEqualTo(swipeDTO)
    }

    private fun insertSwipe(swipeDTO: SwipeDTO): Swipe? {
        if (isSwipeInsertable(swipeDTO)) {
            swipeMapper.toSwipe(swipeDTO)?.let { swipe ->
                swipeDAO.insert(swipe)
                return swipe
            }
        }
        return null
    }

    override suspend fun syncSwipeCount() {
        withContext(ioDispatcher) {
            val response = swipeRDS.countSwipes()
            swipeCountInvalidationListener?.onInvalidate(response.data?.count)
        }
    }

    override suspend fun deleteSwipes() {
        withContext(ioDispatcher) {
            swipeDAO.deleteAll(preferenceProvider.getAccountId())
        }
    }

    override fun getSwipePageInvalidationFlow(): Flow<Boolean> {
        return swipeDAO.getInvalidationFlow()
    }

    override fun test() {
//        CoroutineScope(ioDispatcher).launch {
//            val click = Click(1, UUID.randomUUID(), UUID.randomUUID(), true, "profiel photo");
//            newClickInvalidationListener?.onInvalidate(click)
//        }

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