package com.beeswork.balance.data.network.response.match

import org.threeten.bp.OffsetDateTime
import java.util.*

data class MatchDTO(
    val chatId: Long,
    val matchedId: UUID,
    val active: Boolean,
    val unmatched: Boolean,
    val name: String,
    val repPhotoKey: String?,
    val deleted: Boolean,
    val createdAt: OffsetDateTime?
)