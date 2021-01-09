package com.beeswork.balance.data.network.stomp

class WebSocketLifeCycleEvent(
    private val type: Type
) {



    enum class Type {
        CONNECTED,
        ERROR,
        DISCONNECTED
    }
}