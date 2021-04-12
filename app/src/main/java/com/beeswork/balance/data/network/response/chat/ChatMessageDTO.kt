package com.beeswork.balance.data.network.response.chat

import org.threeten.bp.OffsetDateTime

data class ChatMessageDTO(
    val key: Long = 0,
    val id: Long,
    val chatId: Long = 0,
    val body: String = "",
    val createdAt: OffsetDateTime
)