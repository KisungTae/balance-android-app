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

    override fun toEntity(dto: ChatMessageDTO): ChatMessage? {
        return null
//        return ChatMessage(
//            dto.chatId ?: 0,
//            dto.body ?: "",
//            getStatusByKey(dto.key),
//            dto.createdAt,
//            dto.id ?: 0,
//            dto.key ?: 0,
//        )
    }

    override fun toDomain(entity: ChatMessage): ChatMessageDomain {
        val createdAt = if (entity.isProcessed()) entity.createdAt.atZoneSameInstant(ZoneId.systemDefault()) else null
        return ChatMessageDomain(
            entity.key,
            entity.id,
            entity.body,
            entity.status,
            entity.isProcessed(),
            createdAt?.toLocalDate(),
            createdAt?.toLocalTime()?.truncatedTo(ChronoUnit.MINUTES)
        )

    }
}