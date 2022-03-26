package com.beeswork.balance.data.network.service.stomp

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class WebSocketStateImpl: WebSocketState {
    private var disconnectedByUser = false
    private var webSocketStatus = WebSocketStatus.CLOSED
    private val mutex = Mutex()

    override suspend fun isConnectableAndSetToConnecting(): Boolean {
        mutex.withLock {
            if (webSocketStatus == WebSocketStatus.CLOSED && !disconnectedByUser) {
                webSocketStatus = WebSocketStatus.CONNECTING
                return true
            }
            return false
        }
    }

    override suspend fun reset() {
        mutex.withLock {
            disconnectedByUser = false
            webSocketStatus = WebSocketStatus.CLOSED
        }
    }

    override suspend fun setSocketStatus(webSocketStatus: WebSocketStatus) {
        mutex.withLock {
            this.webSocketStatus = webSocketStatus
        }
    }

    override suspend fun isClosed(): Boolean {
        mutex.withLock {
            return webSocketStatus == WebSocketStatus.CLOSED
        }
    }

    override suspend fun update(disconnectedByUser: Boolean?, webSocketStatus: WebSocketStatus?) {
        mutex.withLock {
            if (disconnectedByUser != null) {
                this.disconnectedByUser = disconnectedByUser
            }

            if (webSocketStatus != null) {
                this.webSocketStatus = webSocketStatus
            }
        }
    }

    override suspend fun isStompConnected(): Boolean {
        mutex.withLock {
            return webSocketStatus == WebSocketStatus.STOMP_CONNECTED
        }
    }

    override suspend fun isDisconnected(): Boolean {
        mutex.withLock {
            return disconnectedByUser
        }
    }

    override suspend fun setDisconnectedByUser(disconnectedByUser: Boolean) {
        mutex.withLock {
            this.disconnectedByUser = disconnectedByUser
        }
    }
}