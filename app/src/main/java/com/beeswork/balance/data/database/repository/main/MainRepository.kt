package com.beeswork.balance.data.database.repository.main

import com.beeswork.balance.data.network.service.stomp.WebSocketEvent
import kotlinx.coroutines.flow.Flow

interface MainRepository {

    val webSocketEventFlow: Flow<WebSocketEvent>

    fun connectStomp(forceToConnect: Boolean)
    fun disconnectStomp()
}