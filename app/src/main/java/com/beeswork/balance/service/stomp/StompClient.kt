package com.beeswork.balance.service.stomp

import androidx.lifecycle.LiveData

interface StompClient {

    val webSocketEventLiveData: LiveData<WebSocketEvent>
    fun connect()
    fun send(chatId: Long, matchedId: String, body: String)
    fun disconnect()
}