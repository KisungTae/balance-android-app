package com.beeswork.balance.domain.usecase.chat

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import java.util.*

interface ResendChatMessageUseCase {
    suspend fun invoke(chatId: UUID, tag: UUID): Resource<EmptyResponse>
}