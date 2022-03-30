package com.beeswork.balance.domain.usecase.chat

import com.beeswork.balance.data.database.repository.chat.ChatRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class DeleteChatMessageUseCaseImpl(
    private val chatRepository: ChatRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
): DeleteChatMessageUseCase {

    override suspend fun invoke(chatId: UUID, tag: UUID) = withContext(defaultDispatcher) {
        chatRepository.deleteChatMessage(chatId, tag)
    }

}