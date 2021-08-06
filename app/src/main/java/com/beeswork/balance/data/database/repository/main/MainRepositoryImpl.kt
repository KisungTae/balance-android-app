package com.beeswork.balance.data.database.repository.main

import com.beeswork.balance.data.network.service.stomp.StompClient
import com.beeswork.balance.data.network.service.stomp.WebSocketEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class MainRepositoryImpl(
    private val stompClient: StompClient,
    private val ioDispatcher: CoroutineDispatcher
): MainRepository {

    private var webSocketEventListener: WebSocketEventListener? = null

    @ExperimentalCoroutinesApi
    override val webSocketEventFlow = callbackFlow<WebSocketEvent> {
        webSocketEventListener = object : WebSocketEventListener {
            override fun onInvoke(webSocketEvent: WebSocketEvent) {
                offer(webSocketEvent)
            }
        }
        awaitClose {  }
    }

    init {
        collectWebSocketEventFlow()
    }

    private fun collectWebSocketEventFlow() {
        stompClient.webSocketEventFlow.onEach { webSocketEvent ->
            webSocketEventListener?.onInvoke(webSocketEvent)
        }
    }


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
}