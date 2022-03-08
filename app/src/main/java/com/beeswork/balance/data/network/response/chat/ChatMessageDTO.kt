package com.beeswork.balance.data.network.response.chat

import org.threeten.bp.OffsetDateTime
import java.util.*

data class ChatMessageDTO(
    val id: UUID?,
    val chatId: UUID,
    val senderId: UUID?,
    val recipientId: UUID?,
    val tag: UUID,
    val body: String,
    val createdAt: OffsetDateTime?
)