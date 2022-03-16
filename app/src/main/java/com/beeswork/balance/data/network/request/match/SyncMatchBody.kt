package com.beeswork.balance.data.network.request.match

import java.util.*

data class SyncMatchBody(
    val chatId: UUID,
    val lastReadReceivedChatMessageId: Long
)