package com.beeswork.balance.data.network.response.chat

import org.threeten.bp.OffsetDateTime
import java.util.*

data class ChatMessageDTO(
    var key: Long?,
    val id: UUID?,
    val chatId: Long?,
    val body: String?,
    val createdAt: OffsetDateTime?,
    val accountId: UUID?,
    val recipientId: UUID?
) {
    constructor(chatId: Long?, body: String?, accountId: UUID?, recipientId: UUID?) : this(
        null,
        null,
        chatId,
        body,
        null,
        accountId,
        recipientId
    )

    constructor(key: Long?, chatId: Long?) : this(key, null, chatId, null, null, null, null)
    constructor(key: Long) : this(key, null, null, null, null, null, null)
    constructor(key: Long, chatId: Long, accountId: UUID, recipientId: UUID, body: String): this(key, null, chatId, body, null, accountId, recipientId)
}