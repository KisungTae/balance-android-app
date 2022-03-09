package com.beeswork.balance.domain.usecase.chat

import com.beeswork.balance.data.database.repository.chat.ChatRepository
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.exception.MatchUnmatchedException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*

class ResendChatMessageUseCaseImpl(
    private val chatRepository: ChatRepository,
    private val matchRepository: MatchRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
): ResendChatMessageUseCase {

    override suspend fun invoke(chatId: UUID, tag: UUID): Resource<EmptyResponse> = withContext(defaultDispatcher) {
        try {
            if (matchRepository.isUnmatched(chatId)) {
                return@withContext Resource.error(MatchUnmatchedException())
            }
            return@withContext chatRepository.resendChatMessage(tag)
        } catch (e: IOException) {
            return@withContext Resource.error(e)
        }
    }
}