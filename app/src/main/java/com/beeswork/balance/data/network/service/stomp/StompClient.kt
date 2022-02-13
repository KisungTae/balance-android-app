package com.beeswork.balance.data.network.service.stomp

import kotlinx.coroutines.flow.Flow

interface StompClient {

    val webSocketEventFlow: Flow<WebSocketEvent>

    fun connect()
    fun disconnect()
}