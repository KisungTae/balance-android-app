package com.beeswork.balance.data.database.repository.main

import com.beeswork.balance.data.database.common.CallBackFlowListener
import com.beeswork.balance.data.database.repository.BaseRepository
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

@ExperimentalCoroutinesApi
class MainRepositoryImpl(
    loginRDS: LoginRDS,
    preferenceProvider: PreferenceProvider,
    private val stompClient: StompClient,
    private val ioDispatcher: CoroutineDispatcher,
    private val applicationScope: CoroutineScope
): BaseRepository(loginRDS, preferenceProvider), MainRepository {

    private lateinit var webSocketEventCallBackFlowListener: CallBackFlowListener<WebSocketEvent>
    override val webSocketEventFlow: Flow<WebSocketEvent> = callbackFlow {
        webSocketEventCallBackFlowListener = object : CallBackFlowListener<WebSocketEvent> {
            override fun onInvoke(data: WebSocketEvent) {
                offer(data)
            }
        }
        awaitClose {  }
    }

    private var reconnectToStompJob: Job? = null


    init {
        applicationScope.launch {
            stompClient.webSocketEventChannel.openSubscription().let { receiveChannel ->
                for (webSocketEvent in receiveChannel) {
                    when (webSocketEvent.status) {
                        WebSocketStatus.ERROR -> scheduleConnectToStomp(webSocketEvent)
                        WebSocketStatus.CLOSED -> scheduleConnectToStomp(webSocketEvent)
                    }
                }
            }
        }
    }

    private suspend fun scheduleConnectToStomp(webSocketEvent: WebSocketEvent) {
        if (ExceptionCode.isExpiredJWTTokenException(webSocketEvent.exception)) {
            val response = doRefreshAccessToken()
            if (response.isSuccess()) {
                reconnectToStompJob?.cancel()
                stompClient.connect(false)
                return
            }

            if (response.isError() && !ExceptionCode.isLoginException(response.exception)) {
                launchReconnectToStompJob()
                return
            }
        }

        if (ExceptionCode.isLoginException(webSocketEvent.exception) || webSocketEvent.exception is NoInternetConnectivityException) {
            reconnectToStompJob?.cancel()
            webSocketEventCallBackFlowListener.onInvoke(webSocketEvent)
            return
        }
        launchReconnectToStompJob()
    }

    private fun launchReconnectToStompJob() {
        if (reconnectToStompJob?.isActive == false) {
            reconnectToStompJob = applicationScope.launch {
                delay(RECONNECT_TO_STOMP_DELAY)
                stompClient.connect(false)
            }
        }
    }

    override fun connectStomp(forceToConnect: Boolean) {
        stompClient.connect(forceToConnect)
    }

    override fun disconnectStomp() {
        stompClient.disconnect()
    }

    companion object {
        const val RECONNECT_TO_STOMP_DELAY = 60000L
    }
}