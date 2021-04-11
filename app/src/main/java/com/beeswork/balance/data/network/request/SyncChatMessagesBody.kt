package com.beeswork.balance.data.network.request

import java.util.*

data class SyncChatMessagesBody(
    val accountId: UUID?,
    val identityToken: UUID?,
    val sentChatMessageIds: List<Long>,
    val receivedChatMessageIds: List<Long>
)