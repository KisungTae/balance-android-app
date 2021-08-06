package com.beeswork.balance.data.database.repository.main

import com.beeswork.balance.data.network.service.stomp.WebSocketEvent

interface WebSocketEventListener {
    fun onInvoke(webSocketEvent: WebSocketEvent)
}