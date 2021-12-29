package com.beeswork.balance.data.network.service.stomp

import androidx.lifecycle.LiveData
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.data.network.response.match.MatchDTO
import com.beeswork.balance.data.network.response.click.ClickDTO
import kotlinx.coroutines.flow.Flow
import java.util.*

interface StompClient {

    val webSocketEventFlow: Flow<WebSocketEvent>

    fun connect()
    fun disconnect()
}