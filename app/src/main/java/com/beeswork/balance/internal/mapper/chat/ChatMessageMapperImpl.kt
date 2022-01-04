package com.beeswork.balance.internal.mapper.chat

import com.beeswork.balance.data.database.entity.chat.ChatMessage
import com.beeswork.balance.data.database.entity.chat.ChatMessageToSendTuple
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.internal.util.safeLet
import com.beeswork.balance.ui.chat.ChatMessageDomain
import org.threeten.bp.ZoneId
import org.threeten.bp.temporal.ChronoUnit
import java.util.*

class ChatMessageMapperImpl : ChatMessageMapper {

    override fun toReceivedChatMessage(chatMessageDTO: ChatMessageDTO): ChatMessage? {
        return safeLet(
            chatMessageDTO.id,
            chatMessageDTO.chatId,
            chatMessageDTO.body,
            chatMessageDTO.createdAt
        ) { id, chatId, body, createdAt ->
            return@safeLet ChatMessage(chatId, body, ChatMessageStatus.RECEIVED, id, createdAt)
        }
    }

    override fun toDomain(chatMessage: ChatMessage): ChatMessageDomain {
        val createdAt = if (chatMessage.status.isProcessed()) {
            chatMessage.createdAt.atZoneSameInstant(ZoneId.systemDefault())
        } else {
            null
        }
        return ChatMessageDomain(
            chatMessage.key,
            chatMessage.id,
            chatMessage.body,
            chatMessage.status,
            createdAt?.toLocalDate(),
            createdAt?.toLocalTime()?.truncatedTo(ChronoUnit.MINUTES)
        )

    }

    override fun toChatMessageDTO(chatMessage: ChatMessage, accountId: UUID, recipientId: UUID): ChatMessageDTO {
        return ChatMessageDTO(chatMessage.id, chatMessage.chatId, chatMessage.body, null, accountId, recipientId)
    }

    override fun toChatMessageDTO(chatMessageToSendTuple: ChatMessageToSendTuple, accountId: UUID): ChatMessageDTO {
        return ChatMessageDTO(
            chatMessageToSendTuple.id,
            chatMessageToSendTuple.chatId,
            chatMessageToSendTuple.body,
            null,
            accountId,
            chatMessageToSendTuple.swipedId
        )
    }
}