package com.beeswork.balance.data.network.response.chat

import org.threeten.bp.OffsetDateTime
import java.util.*

data class ChatMessageReceiptDTO(
    var key: Long?,
    val id: Long?,
    val chatId: Long?,
    val createdAt: OffsetDateTime?,
    val error: String?
) {
    constructor(key: Long?) : this(key, null, null, null, null)
}