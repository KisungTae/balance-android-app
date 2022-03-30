package com.beeswork.balance.domain.usecase.chat

import java.util.*

interface DeleteChatMessageUseCase {
    suspend fun invoke(chatId: UUID, tag: UUID)
}