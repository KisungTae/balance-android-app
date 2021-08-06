package com.beeswork.balance.data.database.repository.main

import com.beeswork.balance.data.network.service.stomp.WebSocketEvent
import kotlinx.coroutines.flow.Flow

interface MainRepository {
    suspend fun connectStomp()
    suspend fun disconnectStomp()
//    fun getWebSocketEventFlow(): Flow<WebSocketEvent>

    val webSocketEventFlow: Flow<WebSocketEvent>
}