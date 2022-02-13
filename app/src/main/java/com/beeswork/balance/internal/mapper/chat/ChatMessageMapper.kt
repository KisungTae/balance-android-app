package com.beeswork.balance.internal.mapper.chat

import com.beeswork.balance.data.database.entity.chat.ChatMessage
import com.beeswork.balance.data.database.entity.chat.ChatMessageToSendTuple
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.ui.chatfragment.ChatMessageDomain
import java.util.*

interface ChatMessageMapper {

    fun toReceivedChatMessage(chatMessageDTO: ChatMessageDTO): ChatMessage?
    fun toDomain(chatMessage: ChatMessage): ChatMessageDomain
    fun toChatMessageDTO(chatMessage: ChatMessage, accountId: UUID, recipientId: UUID): ChatMessageDTO
    fun toChatMessageDTO(chatMessageToSendTuple: ChatMessageToSendTuple, accountId: UUID): ChatMessageDTO
}