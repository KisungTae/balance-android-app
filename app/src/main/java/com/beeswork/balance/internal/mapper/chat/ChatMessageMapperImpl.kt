package com.beeswork.balance.internal.mapper.chat

import com.beeswork.balance.data.database.entity.chat.ChatMessage
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.domain.uistate.chat.ChatMessageItemUIState
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.internal.util.safeLet
import org.threeten.bp.ZoneId
import org.threeten.bp.temporal.ChronoUnit
import java.util.*

class ChatMessageMapperImpl : ChatMessageMapper {

    override fun toReceivedChatMessage(chatMessageDTO: ChatMessageDTO): ChatMessage? {
        return toChatMessage(chatMessageDTO, ChatMessageStatus.RECEIVED)
    }

    override fun toSentChatMessage(chatMessageDTO: ChatMessageDTO): ChatMessage? {
        return toChatMessage(chatMessageDTO, ChatMessageStatus.SENT)
    }

    private fun toChatMessage(chatMessageDTO: ChatMessageDTO, status: ChatMessageStatus): ChatMessage? {
        if (chatMessageDTO.id == null) {
            return null
        }
        return ChatMessage(
            chatMessageDTO.chatId,
            chatMessageDTO.body,
            status,
            chatMessageDTO.tag,
            chatMessageDTO.createdAt,
            chatMessageDTO.id
        )
    }

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
}