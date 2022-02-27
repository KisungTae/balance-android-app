package com.beeswork.balance.data.network.response.match

import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import org.threeten.bp.OffsetDateTime

data class ListMatchesDTO(
    val matchDTOs: List<MatchDTO>,
    val matchCount: Long,
    val matchCountCountedAt: OffsetDateTime
)