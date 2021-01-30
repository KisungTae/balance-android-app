package com.beeswork.balance.data.network.stomp

import androidx.lifecycle.LiveData
import com.beeswork.balance.data.observable.WebSocketEvent

interface StompClient {

    val webSocketEvent: LiveData<WebSocketEvent>
    fun connectChat(chatId: Long, matchedId: String)
    fun send(chatId: Long, matchedId: String, body: String)
    fun disconnectChat()
}