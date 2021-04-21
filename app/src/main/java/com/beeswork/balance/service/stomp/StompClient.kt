package com.beeswork.balance.service.stomp

import androidx.lifecycle.LiveData
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.data.network.response.match.MatchDTO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import java.util.*

interface StompClient {

    val webSocketEventLiveData: LiveData<WebSocketEvent>
    val chatMessageReceiptFlow: Flow<ChatMessageDTO>
    val chatMessageReceivedFlow: Flow<ChatMessageDTO>
    val matchedFlow: Flow<MatchDTO>

    fun connect()
    fun sendChatMessage(key: Long, chatId: Long, matchedId: UUID, body: String)
    fun send(chatId: Long, matchedId: String, body: String)
    fun disconnect()
}