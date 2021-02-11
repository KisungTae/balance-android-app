package com.beeswork.balance.data.network.response.match

import com.beeswork.balance.data.network.response.chat.ChatMessageDTO

data class ListMatchDTO(
    val matchDTOs: List<MatchDTO>,
    val sentChatMessageDTOs: List<ChatMessageDTO>,
    val receivedChatMessageDTOs: List<ChatMessageDTO>
)