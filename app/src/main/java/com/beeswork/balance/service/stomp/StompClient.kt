package com.beeswork.balance.service.stomp

import androidx.lifecycle.LiveData
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.data.network.response.match.MatchDTO
import com.beeswork.balance.data.network.response.swipe.SwipeDTO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import java.util.*

interface StompClient {

    val webSocketEventLiveData: LiveData<WebSocketEvent>
    val chatMessageReceiptFlow: Flow<ChatMessageDTO>
    val chatMessageFlow: Flow<ChatMessageDTO>
    val matchedFlow: Flow<MatchDTO>
    val clickedFlow: Flow<SwipeDTO>

    fun connect()
    fun sendChatMessage(key: Long, chatId: Long, swipedId: UUID, body: String)
    fun disconnect()
}