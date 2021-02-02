package com.beeswork.balance.service.stomp

import androidx.lifecycle.LiveData

interface StompClient {

    val webSocketEvent: LiveData<WebSocketEvent>
    fun connectChat(chatId: Long, matchedId: String)
    fun send(chatId: Long, matchedId: String, body: String)
    fun disconnectChat()
}