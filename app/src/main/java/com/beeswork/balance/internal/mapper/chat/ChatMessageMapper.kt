package com.beeswork.balance.internal.mapper.chat

import com.beeswork.balance.data.database.entity.chat.ChatMessage
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.domain.uistate.chat.ChatMessageItemUIState
import com.beeswork.balance.internal.constant.ChatMessageStatus

interface ChatMessageMapper {

    fun toItemUIState(chatMessage: ChatMessage): ChatMessageItemUIState
    fun toChatMessage(chatMessageDTO: ChatMessageDTO, status: ChatMessageStatus, sequence: Long?): ChatMessage

}