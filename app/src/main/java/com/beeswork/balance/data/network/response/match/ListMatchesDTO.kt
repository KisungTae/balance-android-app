package com.beeswork.balance.data.network.response.match

import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import org.threeten.bp.OffsetDateTime

data class ListMatchesDTO(
    var fetchedAt: OffsetDateTime,
    var matchDTOs: List<MatchDTO>?,
    val sentChatMessageDTOs: List<ChatMessageDTO>?,
    val receivedChatMessageDTOs: List<ChatMessageDTO>?
)