package com.beeswork.balance.data.network.response.chat

import org.threeten.bp.OffsetDateTime
import java.util.*

data class ChatMessageReceiptDTO(
    val id: UUID?,
    val chatId: Long?,
    val createdAt: OffsetDateTime?,
    val error: String?
)