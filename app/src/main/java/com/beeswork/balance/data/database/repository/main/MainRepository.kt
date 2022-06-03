package com.beeswork.balance.data.database.repository.main

import com.beeswork.balance.data.network.service.stomp.WebSocketEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface MainRepository {

    fun getWebSocketEventFlow(): SharedFlow<WebSocketEvent>
    fun connectStomp(forceToConnect: Boolean)
    fun disconnectStomp()
}