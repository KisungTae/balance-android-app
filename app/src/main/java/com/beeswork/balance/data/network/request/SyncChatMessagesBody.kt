package com.beeswork.balance.data.network.request

import java.util.*

data class SyncChatMessagesBody(
    val sentChatMessageIds: List<Long>,
    val receivedChatMessageIds: List<Long>
)