package com.beeswork.balance.service.stomp

import androidx.lifecycle.LiveData
import java.util.*

interface StompClient {

    val webSocketEventLiveData: LiveData<WebSocketEvent>
    fun connect()
    fun sendChatMessage(key: Long, chatId: Long, matchedId: UUID, body: String)
    fun send(chatId: Long, matchedId: String, body: String)
    fun disconnect()
}