package com.beeswork.balance.data.network.stomp

import androidx.lifecycle.LiveData
import com.beeswork.balance.internal.Resource
import io.reactivex.rxjava3.subjects.PublishSubject

interface StompClient {

    val webSocketLifeCycleEvent: LiveData<Resource<WebSocketLifeCycleEvent>>
    val stompFrame: LiveData<Resource<StompFrame>>

    fun connectChat(chatId: Long, matchedId: String)
    fun send(chatId: Long, matchedId: String, message: String)
}