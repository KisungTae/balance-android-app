package com.beeswork.balance.data.network.response.match

import org.threeten.bp.OffsetDateTime
import java.util.*

data class MatchDTO(
    val chatId: Long,
    val matchedId: UUID,
    val unmatched: Boolean,
    val updatedAt: OffsetDateTime,
    val name: String,
    val repPhotoKey: String,
    val blocked: Boolean,
    val deleted: Boolean,
    val accountUpdatedAt: OffsetDateTime,
)