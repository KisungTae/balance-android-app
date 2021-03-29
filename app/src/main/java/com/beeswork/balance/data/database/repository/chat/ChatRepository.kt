package com.beeswork.balance.data.database.repository.chat

import androidx.lifecycle.LiveData
import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.ui.chat.ChatMessageDomain

interface ChatRepository {

    suspend fun sendChatMessage(chatId: Long, body: String)
    suspend fun loadChatMessages(loadSize: Int, startPosition: Int, chatId: Long): List<ChatMessage>

    suspend fun test()
}