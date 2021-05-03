package com.beeswork.balance.data.network.response.match

import com.beeswork.balance.internal.constant.ClickResult
import org.threeten.bp.OffsetDateTime
import java.util.*

data class MatchDTO(
    val clickResult: ClickResult,
    val chatId: Long,
    val matcherId: UUID?,
    val matchedId: UUID,
    val active: Boolean,
    val unmatched: Boolean,
    val name: String,
    val repPhotoKey: String?,
    val createdAt: OffsetDateTime?
)