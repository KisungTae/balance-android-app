package com.beeswork.balance.internal.mapper.chat

import com.beeswork.balance.data.database.entity.chat.ChatMessage
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.ui.chatfragment.ChatMessageDomain

interface ChatMessageMapper {

    fun toReceivedChatMessage(chatMessageDTO: ChatMessageDTO): ChatMessage?
    fun toDomain(chatMessage: ChatMessage): ChatMessageDomain
}