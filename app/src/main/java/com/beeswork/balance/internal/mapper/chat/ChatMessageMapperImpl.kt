package com.beeswork.balance.internal.mapper.chat

import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.ui.chat.ChatMessageDomain

class ChatMessageMapperImpl: ChatMessageMapper {
    override fun fromDTOToEntity(dto: ChatMessageDTO): ChatMessage {
        return ChatMessage(
            dto.messageId ?: 0,
            dto.id,
            dto.chatId ?: 0,
            dto.body ?: "",
            toChatMessageStatusFromMessageId(dto.messageId),
            dto.createdAt,
            dto.createdAt
        )
    }

    override fun fromEntityToDomain(entity: ChatMessage): ChatMessageDomain {
        return ChatMessageDomain(
            entity.messageId,
            entity.id,
            entity.body,
            entity.status,
            entity.createdAt
        )
    }

    private fun toChatMessageStatusFromMessageId(messageId: Long?): ChatMessageStatus {
        return if (messageId == null) ChatMessageStatus.SENT else ChatMessageStatus.RECEIVED
    }
}