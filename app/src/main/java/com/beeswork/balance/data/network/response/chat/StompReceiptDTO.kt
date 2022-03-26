package com.beeswork.balance.data.network.response.chat

import org.threeten.bp.OffsetDateTime
import java.util.*

data class StompReceiptDTO(
    val id: Long?,
    val tag: UUID?,
    val createdAt: OffsetDateTime?,
    val error: String?,
    val errorMessage: String?
)