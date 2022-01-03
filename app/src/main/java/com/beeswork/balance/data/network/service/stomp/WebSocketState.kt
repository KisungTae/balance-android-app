package com.beeswork.balance.data.network.service.stomp

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class WebSocketState {
    private var refreshAccessToken = false
    private var disconnectedByUser = false
    private var reconnect = true
    private var webSocketStatus = WebSocketStatus.CLOSED
    private val mutex = Mutex()

    suspend fun isConnectableAndSetToConnecting(): Boolean {
        mutex.withLock {
            if (webSocketStatus == WebSocketStatus.CLOSED && reconnect && !disconnectedByUser) {
                webSocketStatus = WebSocketStatus.CONNECTING
                return true
            }
            return false
        }
    }

    suspend fun setReconnect(reconnect: Boolean) {
        mutex.withLock {
            this.reconnect = reconnect
        }
    }

    suspend fun isRefreshAccessToken(): Boolean {
        mutex.withLock {
            return refreshAccessToken
        }
    }

    suspend fun isReconnect(): Boolean {
        mutex.withLock {
            return reconnect
        }
    }

    suspend fun setRefreshAccessToken(refreshAccessToken: Boolean) {
        mutex.withLock {
            this.refreshAccessToken = refreshAccessToken
        }
    }

    suspend fun reset() {
        mutex.withLock {
            disconnectedByUser = false
            reconnect = true
            refreshAccessToken = false
            webSocketStatus = WebSocketStatus.CLOSED
        }
    }

    suspend fun setSocketStatus(webSocketStatus: WebSocketStatus) {
        mutex.withLock {
            this.webSocketStatus = webSocketStatus
        }
    }

    suspend fun isClosed(): Boolean {
        mutex.withLock {
            return webSocketStatus == WebSocketStatus.CLOSED
        }
    }

    suspend fun update(reconnect: Boolean?, refreshAccessToken: Boolean?, disconnectedByUser: Boolean?, webSocketStatus: WebSocketStatus?) {
        mutex.withLock {
            if (reconnect != null) {
                this.reconnect = reconnect
            }

            if (refreshAccessToken != null) {
                this.refreshAccessToken = refreshAccessToken
            }

            if (disconnectedByUser != null) {
                this.disconnectedByUser = disconnectedByUser
            }

            if (webSocketStatus != null) {
                this.webSocketStatus = webSocketStatus
            }

        }
    }

    suspend fun isStompConnected(): Boolean {
        mutex.withLock {
            return webSocketStatus == WebSocketStatus.STOMP_CONNECTED
        }
    }

    suspend fun isDisconnected(): Boolean {
        mutex.withLock {
            return !reconnect || disconnectedByUser
        }
    }
}