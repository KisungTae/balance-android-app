package com.beeswork.balance.data.network.response.chat

import org.threeten.bp.OffsetDateTime
import java.util.*

data class ChatMessageDTO(
    val id: UUID?,
    val chatId: Long?,
    val body: String?,
    val createdAt: OffsetDateTime?,
    val accountId: UUID?,
    val recipientId: UUID?
)