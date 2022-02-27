package com.beeswork.balance.data.database.common

import org.threeten.bp.OffsetDateTime
import java.util.concurrent.ConcurrentHashMap

class PageSyncDateTracker {

    private val pageFetchDates = ConcurrentHashMap<String, OffsetDateTime?>()

    fun shouldSyncPage(key: Any): Boolean {
        val fetchedAt = pageFetchDates[key.toString()]?.plusMinutes(PAGE_FETCH_SYNC_IN_MINUTES) ?: return true
        return fetchedAt.isBefore(OffsetDateTime.now())
    }

    fun updateSyncDate(key: Any, syncedAt: OffsetDateTime?) {
        pageFetchDates[key.toString()] = syncedAt
    }

    companion object {
        const val PAGE_FETCH_SYNC_IN_MINUTES = 1L
    }
}