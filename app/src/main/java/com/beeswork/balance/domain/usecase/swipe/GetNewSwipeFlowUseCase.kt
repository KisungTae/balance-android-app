package com.beeswork.balance.domain.usecase.swipe

import com.beeswork.balance.data.database.entity.swipe.Swipe
import kotlinx.coroutines.flow.Flow

interface GetNewSwipeFlowUseCase {

    suspend fun invoke(): Flow<Swipe>
}