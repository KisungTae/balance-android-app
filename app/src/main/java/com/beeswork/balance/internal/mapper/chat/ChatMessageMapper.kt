package com.beeswork.balance.internal.mapper.chat

import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.internal.mapper.common.Mapper
import com.beeswork.balance.ui.chat.ChatMessageDomain
import java.util.*

interface ChatMessageMapper {

    fun toReceivedChatMessage(chatMessageDTO: ChatMessageDTO): ChatMessage?
    fun toSentChatMessage(chatMessage: ChatMessage?, chatMessageDTO: ChatMessageDTO): ChatMessage?
    fun toDomain(chatMessage: ChatMessage): ChatMessageDomain
}