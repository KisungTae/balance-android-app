package com.beeswork.balance.data.network.response.chat

class ListChatMessagesDTO(
    val sentChatMessageDTOs: List<ChatMessageDTO> = emptyList(),
    val receivedChatMessageDTOs: List<ChatMessageDTO> = emptyList()
) {
}