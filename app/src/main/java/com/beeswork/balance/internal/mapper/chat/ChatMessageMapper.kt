package com.beeswork.balance.internal.mapper.chat

import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.internal.mapper.common.Mapper
import com.beeswork.balance.ui.chat.ChatMessageDomain

interface ChatMessageMapper: Mapper<ChatMessageDTO, ChatMessage, ChatMessageDomain>