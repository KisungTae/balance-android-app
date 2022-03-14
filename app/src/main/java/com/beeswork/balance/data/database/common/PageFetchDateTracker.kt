package com.beeswork.balance.data.database.common

import org.threeten.bp.OffsetDateTime
import java.util.concurrent.ConcurrentHashMap

class PageFetchDateTracker(
    private val refetchTimeInMinutes: Long
) {

    private val pageFetchDates = ConcurrentHashMap<String, OffsetDateTime?>()

    fun shouldFetchPage(key: Any): Boolean {
        val fetchedAt = pageFetchDates[key.toString()]?.plusMinutes(refetchTimeInMinutes) ?: return true
        return fetchedAt.isBefore(OffsetDateTime.now())
    }

    fun updateFetchDate(key: Any, fetchedAt: OffsetDateTime?) {
        pageFetchDates[key.toString()] = fetchedAt
    }
}