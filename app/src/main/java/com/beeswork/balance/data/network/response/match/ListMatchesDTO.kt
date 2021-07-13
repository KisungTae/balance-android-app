package com.beeswork.balance.data.network.response.match

import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import org.threeten.bp.OffsetDateTime

data class ListMatchesDTO(
    val fetchedAt: OffsetDateTime,
    val matchDTOs: List<MatchDTO>?
)