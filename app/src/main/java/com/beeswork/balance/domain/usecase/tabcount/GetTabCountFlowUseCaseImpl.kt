package com.beeswork.balance.domain.usecase.tabcount

import com.beeswork.balance.data.database.entity.tabcount.TabCount
import com.beeswork.balance.data.database.repository.tabcount.TabCountRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class GetTabCountFlowUseCaseImpl(
    private val tabCountRepository: TabCountRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : GetTabCountFlowUseCase {

    override suspend fun invoke(): Flow<List<TabCount>> = withContext(defaultDispatcher) {
        return@withContext tabCountRepository.getTabCountFlow()
    }
}