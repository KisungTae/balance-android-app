package com.beeswork.balance.data.network.response.chat

import org.threeten.bp.OffsetDateTime

data class ChatMessageDTO(
    val id: Long,
    val body: String?,
    val messageId: Long?,
    val chatId: Long?,
    val createdAt: OffsetDateTime
)