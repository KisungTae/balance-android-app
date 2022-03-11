package com.beeswork.balance.internal.mapper.chat

import com.beeswork.balance.data.database.entity.chat.ChatMessage
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.domain.uistate.chat.ChatMessageItemUIState

interface ChatMessageMapper {

    fun toReceivedChatMessage(chatMessageDTO: ChatMessageDTO): ChatMessage?
    fun toSentChatMessage(chatMessageDTO: ChatMessageDTO): ChatMessage?
    fun toItemUIState(chatMessage: ChatMessage): ChatMessageItemUIState
}