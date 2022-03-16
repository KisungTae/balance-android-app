package com.beeswork.balance.domain.usecase.chat

import java.util.*

interface SyncMatchUseCase {

    suspend fun invoke(chatId: UUID)
}