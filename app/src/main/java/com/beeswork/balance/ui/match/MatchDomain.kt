package com.beeswork.balance.ui.match

import org.threeten.bp.OffsetDateTime
import java.util.*

data class MatchDomain(
    val chatId: Long,
    val matchedId: UUID,
    val unmatched: Boolean,
    val updatedAt: OffsetDateTime,
    val name: String,
    val repPhotoKey: String,
    val blocked: Boolean,
    val deleted: Boolean,
    val accountUpdatedAt: OffsetDateTime,
    val chatMessageReadAt: OffsetDateTime,
    val accountViewedAt: OffsetDateTime
)