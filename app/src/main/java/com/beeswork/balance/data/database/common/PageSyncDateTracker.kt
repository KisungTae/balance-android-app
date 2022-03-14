package com.beeswork.balance.data.database.common

import org.threeten.bp.OffsetDateTime
import java.util.concurrent.ConcurrentHashMap

class PageSyncDateTracker(
    private val syncTimeInMinutes: Long
) {

    private val pageFetchDates = ConcurrentHashMap<String, OffsetDateTime?>()

    fun shouldSyncPage(key: Any): Boolean {
        val fetchedAt = pageFetchDates[key.toString()]?.plusMinutes(syncTimeInMinutes) ?: return true
        return fetchedAt.isBefore(OffsetDateTime.now())
    }

    fun updateSyncDate(key: Any, syncedAt: OffsetDateTime?) {
        pageFetchDates[key.toString()] = syncedAt
    }
}