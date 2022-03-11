package com.beeswork.balance.data.network.response.chat

import org.threeten.bp.OffsetDateTime
import java.util.*

data class ChatMessageDTO(
    val id: Long?,
    val chatId: UUID,
    val senderId: UUID?,
    val tag: UUID?,
    val body: String,
    val createdAt: OffsetDateTime?
)