package com.beeswork.balance.data.network.service.stomp

import androidx.lifecycle.LiveData
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.data.network.response.match.MatchDTO
import com.beeswork.balance.data.network.response.click.ClickDTO
import kotlinx.coroutines.flow.Flow
import java.util.*

interface StompClient {

    val webSocketEventLiveData: LiveData<WebSocketEvent>
    val chatMessageReceiptFlow: Flow<ChatMessageDTO>
    val chatMessageFlow: Flow<ChatMessageDTO>
    val matchFlow: Flow<MatchDTO>
    val clickFlow: Flow<ClickDTO>

    fun connect()
    fun sendChatMessage(key: Long, chatId: Long, swipedId: UUID, body: String)
    fun disconnect()
}