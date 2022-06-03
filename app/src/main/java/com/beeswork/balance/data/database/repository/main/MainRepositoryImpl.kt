package com.beeswork.balance.data.database.repository.main

import com.beeswork.balance.data.database.common.CallBackFlowListener
import com.beeswork.balance.data.database.repository.BaseRepository
import com.beeswork.balance.data.database.repository.chat.ChatRepository
import com.beeswork.balance.data.network.rds.login.LoginRDS
import com.beeswork.balance.data.network.service.stomp.StompClient
import com.beeswork.balance.data.network.service.stomp.WebSocketEvent
import com.beeswork.balance.data.network.service.stomp.WebSocketStatus
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.exception.NoInternetConnectivityException
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

class MainRepositoryImpl(
    loginRDS: LoginRDS,
    preferenceProvider: PreferenceProvider,
    private val stompClient: StompClient,
    private val ioDispatcher: CoroutineDispatcher,
    private val applicationScope: CoroutineScope
) : BaseRepository(loginRDS, preferenceProvider), MainRepository {

    private var reconnectToStompJob: Job? = null


    init {
        stompClient.webSocketEventFlow.onEach { webSocketEvent ->
            when (webSocketEvent.status) {
                WebSocketStatus.ERROR -> reconnectToStomp(webSocketEvent)
                WebSocketStatus.CLOSED -> reconnectToStomp(webSocketEvent)
            }
        }.launchIn(applicationScope)
    }

    private suspend fun reconnectToStomp(webSocketEvent: WebSocketEvent) {
        if (ExceptionCode.isExpiredJWTTokenException(webSocketEvent.exception)) {
            reconnectToStompJob?.cancel()
            val response = doRefreshAccessToken()
            if (response.isSuccess()) {
                reconnectToStompJob?.cancel()
                stompClient.connect(false)
            } else {
                scheduleReconnectToStomp(WebSocketEvent(webSocketEvent.status, response.exception))
            }
        } else {
            scheduleReconnectToStomp(webSocketEvent)
        }
    }

    private fun scheduleReconnectToStomp(webSocketEvent: WebSocketEvent) {
        println("scheduleReconnectToStomp")
        if (ExceptionCode.isLoginException(webSocketEvent.exception) || webSocketEvent.exception is NoInternetConnectivityException) {
            println("ExceptionCode.isLoginException(webSocketEvent.exception) || webSocketEvent.exception is NoInternetConnectivityException")
            reconnectToStompJob?.cancel()
            return
        }

        if (reconnectToStompJob == null || reconnectToStompJob?.isActive == false) {
            println("reconnectToStompJob?.isActive == false")
            reconnectToStompJob = applicationScope.launch {
                delay(RECONNECT_TO_STOMP_DELAY)
                stompClient.connect(false)
            }
        }
    }

    override fun getWebSocketEventFlow(): SharedFlow<WebSocketEvent> {
        return stompClient.webSocketEventFlow
    }

    override fun connectStomp(forceToConnect: Boolean) {
//        stompClient.connect(forceToConnect)
    }

    override fun disconnectStomp() {
//        stompClient.disconnect()
    }

    companion object {
        const val RECONNECT_TO_STOMP_DELAY = 60000L
    }
}