package com.beeswork.balance.data.database.common

import org.threeten.bp.OffsetDateTime
import java.util.concurrent.ConcurrentHashMap

class PageFetchDateTracker {

    private val pageFetchDates = ConcurrentHashMap<Int, OffsetDateTime>()

    fun shouldFetchPage(pageIndex: Int): Boolean {
        val fetchedAt = pageFetchDates[pageIndex]?.plusMinutes(PAGE_FETCH_DELAY_IN_MINUTES) ?: return true
        return fetchedAt.isBefore(OffsetDateTime.now())
    }

    fun updateFetchDate(pageIndex: Int) {
        pageFetchDates[pageIndex] = OffsetDateTime.now()
    }

    companion object {
        const val PAGE_FETCH_DELAY_IN_MINUTES = 1L
    }
}