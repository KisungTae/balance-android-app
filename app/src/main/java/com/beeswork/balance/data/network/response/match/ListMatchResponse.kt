package com.beeswork.balance.data.network.response.match

import com.beeswork.balance.data.network.response.chat.ChatMessageResponse
import org.threeten.bp.OffsetDateTime

data class ListMatchResponse(
    val matchResponses: List<MatchResponse>,
    val chatMessageResponses: List<ChatMessageResponse>,
    val lastAccountUpdatedAt: OffsetDateTime
)