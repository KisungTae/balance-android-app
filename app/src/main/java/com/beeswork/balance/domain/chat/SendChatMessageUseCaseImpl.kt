package com.beeswork.balance.domain.chat

import com.beeswork.balance.data.database.repository.chat.ChatRepository
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.exception.ChatMessageEmptyException
import com.beeswork.balance.internal.exception.ChatMessageSizeLimitExceededException
import com.beeswork.balance.internal.exception.MatchUnmatchedException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*

class SendChatMessageUseCaseImpl(
    private val chatRepository: ChatRepository,
    private val matchRepository: MatchRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : SendChatMessageUseCase {

    override suspend fun sendChatMessage(chatId: UUID, body: String): Resource<EmptyResponse> =
        withContext(defaultDispatcher) {
            try {
                if (matchRepository.isUnmatched(chatId)) {
                    return@withContext Resource.error(MatchUnmatchedException())
                }
                val bodySize = body.toByteArray().size
                if (bodySize > MAX_CHAT_MESSAGE_SIZE) {
                    return@withContext Resource.error(ChatMessageSizeLimitExceededException())
                } else if (bodySize <= 0) {
                    return@withContext Resource.error(ChatMessageEmptyException())
                }
                return@withContext chatRepository.sendChatMessage(chatId, body)
            } catch (e: IOException) {
                return@withContext Resource.error(e)
            }
        }

    companion object {
        private const val MAX_CHAT_MESSAGE_SIZE = 500
    }
}