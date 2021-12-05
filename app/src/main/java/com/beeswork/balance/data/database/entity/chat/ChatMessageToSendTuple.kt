package com.beeswork.balance.data.database.entity.chat

import java.util.*

class ChatMessageToSendTuple(
    val key: Long,
    val chatId: Long,
    val body: String,
    val swipedId: UUID
)