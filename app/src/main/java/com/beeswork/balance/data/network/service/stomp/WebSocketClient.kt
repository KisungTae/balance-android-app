package com.beeswork.balance.data.network.service.stomp

interface WebSocketClient {

    suspend fun connect()
}