package com.beeswork.balance.data.network.stomp

import androidx.lifecycle.LiveData
import com.beeswork.balance.internal.Resource

interface StompClient {

    val webSocketLifeCycleEvent: LiveData<WebSocketLifeCycleEvent>
    fun connectChat(chatId: Long, matchedId: String)
    fun send(chatId: Long, matchedId: String, body: String)
    fun disconnectChat()
}