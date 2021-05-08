package com.beeswork.balance.data.network.service.stomp

import androidx.lifecycle.LiveData
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.data.network.response.match.MatchDTO
import com.beeswork.balance.data.network.response.swipe.ClickDTO
import kotlinx.coroutines.flow.Flow
import java.util.*

interface StompClient {

    val webSocketEventLiveData: LiveData<WebSocketEvent>
    val chatMessageReceiptFlow: Flow<ChatMessageDTO>
    val chatMessageReceivedFlow: Flow<ChatMessageDTO>
    val matchedFlow: Flow<MatchDTO>
    val clickedFlow: Flow<ClickDTO>

    fun connect()
    fun sendChatMessage(key: Long, chatId: Long, swipedId: UUID, body: String)
    fun disconnect()
}