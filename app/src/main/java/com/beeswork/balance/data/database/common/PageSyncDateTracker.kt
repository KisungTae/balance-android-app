package com.beeswork.balance.data.database.common

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.threeten.bp.OffsetDateTime
import java.util.concurrent.ConcurrentHashMap

class PageSyncDateTracker {

    private val pageFetchDates = ConcurrentHashMap<Int, OffsetDateTime?>()

    fun shouldSyncPage(pageIndex: Int): Boolean {
        val fetchedAt = pageFetchDates[pageIndex]?.plusMinutes(PAGE_FETCH_DELAY_IN_MINUTES) ?: return true
        return fetchedAt.isBefore(OffsetDateTime.now())
    }

    fun updateSyncDate(pageIndex: Int, syncedAt: OffsetDateTime?) {
        pageFetchDates[pageIndex] = syncedAt
    }

    companion object {
        const val PAGE_FETCH_DELAY_IN_MINUTES = 1L
    }
}