package com.beeswork.balance.domain.chat

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import java.util.*

interface SendChatMessageUseCase {

    suspend fun sendChatMessage(chatId: UUID, body: String): Resource<EmptyResponse>

}