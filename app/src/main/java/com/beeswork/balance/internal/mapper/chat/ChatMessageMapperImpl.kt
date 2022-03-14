package com.beeswork.balance.internal.mapper.chat

import com.beeswork.balance.data.database.entity.chat.ChatMessage
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.domain.uistate.chat.ChatMessageItemUIState
import com.beeswork.balance.internal.constant.ChatMessageStatus
import org.threeten.bp.ZoneId
import org.threeten.bp.temporal.ChronoUnit

class ChatMessageMapperImpl : ChatMessageMapper {

    override fun toItemUIState(chatMessage: ChatMessage): ChatMessageItemUIState {
        val createdAt = if (chatMessage.status.isProcessed()) {
            chatMessage.createdAt?.atZoneSameInstant(ZoneId.systemDefault())
        } else {
            null
        }
        return ChatMessageItemUIState(
            chatMessage.tag,
            chatMessage.body,
            chatMessage.status,
            createdAt?.toLocalDate(),
            createdAt?.toLocalTime()?.truncatedTo(ChronoUnit.MINUTES)
        )
    }

    override fun toChatMessage(chatMessageDTO: ChatMessageDTO, status: ChatMessageStatus, sequence: Long?): ChatMessage {
        return ChatMessage(
            chatMessageDTO.chatId,
            chatMessageDTO.body,
            status,
            chatMessageDTO.tag,
            chatMessageDTO.createdAt,
            chatMessageDTO.id ?: Long.MAX_VALUE,
            sequence ?: 0
        )
    }
}