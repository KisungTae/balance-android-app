package com.beeswork.balance.data.network.request.chat

import java.util.*

data class SyncChatMessagesBody(
    val sentChatMessageIds: List<Long>,
    val receivedChatMessageIds: List<Long>
)