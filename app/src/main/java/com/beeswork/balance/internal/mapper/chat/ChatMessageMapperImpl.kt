package com.beeswork.balance.internal.mapper.chat

import com.beeswork.balance.data.database.entity.chat.ChatMessage
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.internal.util.safeLet
import com.beeswork.balance.ui.chatfragment.ChatMessageDomain
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
//        val createdAt = if (chatMessage.status.isProcessed()) {
//            chatMessage.createdAt.atZoneSameInstant(ZoneId.systemDefault())
//        } else {
//            null
//        }
//        return ChatMessageDomain(
//            chatMessage.key,
//            chatMessage.id,
//            chatMessage.body,
//            chatMessage.status,
//            createdAt?.toLocalDate(),
//            createdAt?.toLocalTime()?.truncatedTo(ChronoUnit.MINUTES)
//        )
        TODO("Not yet implemented")
    }
}