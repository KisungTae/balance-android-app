package com.beeswork.balance.data.network.service.stomp

interface WebSocketState {
    suspend fun isConnectableAndSetToConnecting(): Boolean
    suspend fun reset()
    suspend fun setSocketStatus(webSocketStatus: WebSocketStatus)
    suspend fun isClosed(): Boolean
    suspend fun update(disconnectedByUser: Boolean?, webSocketStatus: WebSocketStatus?)
    suspend fun isStompConnected(): Boolean
    suspend fun isDisconnected(): Boolean
    suspend fun setDisconnectedByUser(disconnectedByUser: Boolean)
}