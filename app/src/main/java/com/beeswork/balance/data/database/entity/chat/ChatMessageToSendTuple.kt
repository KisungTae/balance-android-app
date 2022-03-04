package com.beeswork.balance.data.database.entity.chat

import java.util.*

class ChatMessageToSendTuple(
    val id: UUID,
    val chatId: UUID,
    val body: String,
    val swipedId: UUID
)