package com.beeswork.balance.data.network.request.chat

import java.util.*

data class SyncChatMessagesBody(
    val chatId: UUID,
    val appToken: UUID,
    val chatMessageIds: List<Long>
)