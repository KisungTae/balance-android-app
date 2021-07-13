package com.beeswork.balance.data.network.response.chat

data class ListChatMessagesDTO(
    val sentChatMessageDTOs: List<ChatMessageDTO>?,
    val receivedChatMessageDTOs: List<ChatMessageDTO>?
)