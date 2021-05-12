package com.beeswork.balance.internal.mapper.chat

import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.internal.util.safeLet
import com.beeswork.balance.ui.chat.ChatMessageDomain
import org.threeten.bp.ZoneId
import org.threeten.bp.temporal.ChronoUnit

class ChatMessageMapperImpl : ChatMessageMapper {

    override fun toReceivedChatMessage(chatMessageDTO: ChatMessageDTO): ChatMessage? {
        return safeLet(
            chatMessageDTO.id,
            chatMessageDTO.chatId,
            chatMessageDTO.body,
            chatMessageDTO.createdAt
        ) { id, chatId, body, createdAt ->
            return@safeLet ChatMessage(chatId, body, ChatMessageStatus.RECEIVED, createdAt, id)
        }
    }

    override fun toSentChatMessage(chatMessage: ChatMessage?, chatMessageDTO: ChatMessageDTO): ChatMessage? {
        return safeLet(chatMessage, chatMessageDTO.id, chatMessageDTO.createdAt) { _chatMessage, id, createdAt ->
            _chatMessage.id = id
            _chatMessage.status = ChatMessageStatus.SENT
            _chatMessage.createdAt = createdAt
            return@safeLet _chatMessage
        }
    }

    override fun toDomain(chatMessage: ChatMessage): ChatMessageDomain {
        val createdAt = if (chatMessage.isProcessed()) chatMessage.createdAt.atZoneSameInstant(ZoneId.systemDefault()) else null
        return ChatMessageDomain(
            chatMessage.key,
            chatMessage.id,
            chatMessage.body,
            chatMessage.status,
            chatMessage.isProcessed(),
            createdAt?.toLocalDate(),
            createdAt?.toLocalTime()?.truncatedTo(ChronoUnit.MINUTES)
        )

    }
}