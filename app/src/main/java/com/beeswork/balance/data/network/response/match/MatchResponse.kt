package com.beeswork.balance.data.network.response.match

import org.threeten.bp.OffsetDateTime

data class MatchResponse(
    val chatId: Long?,
    val matchedId: String?,
    val updatedAt: OffsetDateTime?,
    val unmatched: Boolean?,
    val name: String?,
    val repPhotoKey: String?,
    val blocked: Boolean?,
    val deleted: Boolean?
)