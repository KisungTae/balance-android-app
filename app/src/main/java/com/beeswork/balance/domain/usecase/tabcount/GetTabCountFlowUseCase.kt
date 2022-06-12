package com.beeswork.balance.domain.usecase.tabcount

import com.beeswork.balance.data.database.entity.tabcount.TabCount
import kotlinx.coroutines.flow.Flow

interface GetTabCountFlowUseCase {

    suspend fun invoke(): Flow<List<TabCount>>
}