package com.beeswork.balance.data.network.response.chat

import org.threeten.bp.OffsetDateTime
import java.util.*

data class StompReceiptDTO(
    val id: Long?,
    val tag: UUID?,
    val firstMessage: Boolean?,
    val createdAt: OffsetDateTime?,
    val error: String?,
    val errorMessage: String?
) {
    constructor(tag: UUID, error: String?, errorMessage: String?) : this(null, tag, false, null, error, errorMessage)
}