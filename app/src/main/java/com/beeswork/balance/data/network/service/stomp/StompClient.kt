package com.beeswork.balance.data.network.service.stomp

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.data.network.response.chat.StompReceiptDTO
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.match.MatchDTO
import com.beeswork.balance.data.network.response.swipe.SwipeDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import org.threeten.bp.OffsetDateTime

interface StompClient {

    val webSocketEventFlow: SharedFlow<WebSocketEvent>

//    @ExperimentalCoroutinesApi
//    val webSocketEventChannel: BroadcastChannel<WebSocketEvent>

    val swipeFlow: Flow<SwipeDTO>
    val matchFlow: Flow<MatchDTO>
    val chatMessageFlow: Flow<ChatMessageDTO>
    val stompReceiptFlow: Flow<StompReceiptDTO>


    fun getStompReconnectedAt(): OffsetDateTime?
    fun connect(forceToConnect: Boolean)
    fun disconnect()
    suspend fun sendChatMessage(chatMessageDTO: ChatMessageDTO): Resource<EmptyResponse>
}