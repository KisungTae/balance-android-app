package com.beeswork.balance.internal.mapper.chat

import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.ui.chat.ChatMessageDomain

class ChatMessageMapperImpl : ChatMessageMapper {
    override fun fromDTOToEntity(dto: ChatMessageDTO): ChatMessage {
        return ChatMessage(
            dto.chatId ?: 0,
            dto.body ?: "",
            toChatMessageStatusFromMessageId(dto.key),
            dto.createdAt,
            dto.id,
            dto.key ?: 0,
        )
    }

    override fun fromEntityToDomain(entity: ChatMessage): ChatMessageDomain {
        return ChatMessageDomain(
            entity.key,
            entity.id,
            entity.body,
            entity.status,
            entity.createdAt
        )
    }

    private fun toChatMessageStatusFromMessageId(messageId: Long?): ChatMessageStatus {
        return messageId?.let {
            ChatMessageStatus.SENT
        } ?: ChatMessageStatus.RECEIVED
    }
}