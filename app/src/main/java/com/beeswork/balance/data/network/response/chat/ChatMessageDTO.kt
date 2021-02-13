package com.beeswork.balance.data.network.response.chat

import org.threeten.bp.OffsetDateTime

data class ChatMessageDTO(
    val messageId: Long?,
    val id: Long,
    val chatId: Long?,
    val body: String?,
    val createdAt: OffsetDateTime
)