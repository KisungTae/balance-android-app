package com.beeswork.balance.data.network.response

import com.beeswork.balance.data.database.entity.ChatMessage
import org.threeten.bp.OffsetDateTime

class ChatMessageResponse(
    val messageId: Long?,
    val id: Long,
    val chatId: Long,
    val body: String,
    val createdAt: OffsetDateTime
)