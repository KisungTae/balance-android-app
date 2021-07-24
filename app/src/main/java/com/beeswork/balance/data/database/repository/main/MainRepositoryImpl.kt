package com.beeswork.balance.data.database.repository.main

import com.beeswork.balance.data.network.service.stomp.StompClient
import com.beeswork.balance.data.network.service.stomp.WebSocketEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class MainRepositoryImpl(
    private val stompClient: StompClient,
    private val ioDispatcher: CoroutineDispatcher
): MainRepository {

    override suspend fun connectStomp() {
        withContext(ioDispatcher) {
            stompClient.connect()
        }
    }

    override suspend fun disconnectStomp() {
        withContext(ioDispatcher) {
            stompClient.disconnect()
        }
    }

    override fun getWebSocketEventFlow(): Flow<WebSocketEvent> {
        return stompClient.webSocketEventFlow
    }

}