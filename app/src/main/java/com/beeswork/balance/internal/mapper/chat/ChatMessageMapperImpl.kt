package com.beeswork.balance.internal.mapper.chat

import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.ui.chat.ChatMessageDomain
import org.threeten.bp.ZoneId
import org.threeten.bp.temporal.ChronoUnit

class ChatMessageMapperImpl : ChatMessageMapper {

    override fun fromDTOToEntity(dto: ChatMessageDTO): ChatMessage {
        return ChatMessage(
            dto.chatId ?: 0,
            dto.body ?: "",
            getStatusByKey(dto.key),
            dto.createdAt,
            dto.id ?: 0,
            dto.key ?: 0,
        )
    }

    override fun fromEntityToDomain(entity: ChatMessage): ChatMessageDomain {
        val zonedDateTime = entity.createdAt?.atZoneSameInstant(ZoneId.systemDefault())
        return ChatMessageDomain(
            entity.key,
            entity.id,
            entity.body,
            entity.status,
            zonedDateTime?.toLocalDate(),
            zonedDateTime?.toLocalTime()?.truncatedTo(ChronoUnit.MINUTES)
        )

    }

    private fun getStatusByKey(key: Long?): ChatMessageStatus {
        return key?.let {
            ChatMessageStatus.SENT
        } ?: ChatMessageStatus.RECEIVED
    }
}