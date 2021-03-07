package com.beeswork.balance.data.network.request

import java.util.*

data class SyncChatMessagesBody(
    val accountId: UUID,
    val identityToken: UUID,
    val chatMessageIds: List<Long>,
)