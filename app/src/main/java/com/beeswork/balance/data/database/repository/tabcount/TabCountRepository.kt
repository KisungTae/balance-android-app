package com.beeswork.balance.data.database.repository.tabcount

import com.beeswork.balance.data.database.entity.tabcount.TabCount
import com.beeswork.balance.internal.constant.TabPosition
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.OffsetDateTime

interface TabCountRepository {

    fun getTabCountFlow(): Flow<List<TabCount>>
    suspend fun updateTabCount(tabPosition: TabPosition, count: Long, countedAt: OffsetDateTime)
}