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
        return safeLet(
            chatMessageDTO.id,
            chatMessageDTO.chatId,
            chatMessageDTO.body,
            chatMessageDTO.createdAt
        ) { id, chatId, body, createdAt ->
            return@safeLet ChatMessage(chatId, body, ChatMessageStatus.RECEIVED, id, createdAt)
        }
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