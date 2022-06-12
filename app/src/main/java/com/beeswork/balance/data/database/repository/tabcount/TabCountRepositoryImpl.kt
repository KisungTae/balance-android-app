package com.beeswork.balance.data.database.repository.tabcount

import com.beeswork.balance.data.database.dao.TabCountDAO
import com.beeswork.balance.data.database.entity.tabcount.TabCount
import com.beeswork.balance.internal.constant.TabPosition
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.threeten.bp.OffsetDateTime

class TabCountRepositoryImpl(
    private val tabCountDAO: TabCountDAO,
    private val preferenceProvider: PreferenceProvider,
    private val ioDispatcher: CoroutineDispatcher
) : TabCountRepository {

    override fun getTabCountFlow(): Flow<List<TabCount>> {
        return tabCountDAO.getFlowBy(preferenceProvider.getAccountId())
    }

    override suspend fun updateTabCount(tabPosition: TabPosition, count: Long, countedAt: OffsetDateTime) {
        withContext(ioDispatcher) {
            val accountId = preferenceProvider.getAccountId() ?: return@withContext
            val tabCount = tabCountDAO.getBy(accountId, tabPosition)
            if (tabCount == null) {
                tabCountDAO.insert(TabCount(accountId, tabPosition, count, countedAt))
            } else if (countedAt.isAfter(tabCount.countedAt)) {
                tabCount.count = count
                tabCount.countedAt = countedAt
                tabCountDAO.insert(tabCount)
            }
        }
    }
}