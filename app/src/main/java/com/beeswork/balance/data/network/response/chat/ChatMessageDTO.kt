package com.beeswork.balance.data.network.response.chat

import org.threeten.bp.OffsetDateTime
import java.util.*

data class ChatMessageDTO(
    var key: Long?,
    val id: Long?,
    val chatId: Long?,
    val body: String?,
    val createdAt: OffsetDateTime?,
    val recipientId: UUID?
)