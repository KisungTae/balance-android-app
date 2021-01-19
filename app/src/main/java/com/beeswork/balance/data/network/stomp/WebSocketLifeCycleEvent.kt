package com.beeswork.balance.data.network.stomp

import com.google.gson.JsonPrimitive

class WebSocketLifeCycleEvent(
    private val type: Type,
    private val error: String?,
    private val errorMessage: String?
) {



    companion object {
        fun error(error: String?, errorMessage: String?): WebSocketLifeCycleEvent {
            return WebSocketLifeCycleEvent(Type.ERROR, error, errorMessage)
        }
    }


    enum class Type {
        CONNECTED,
        ERROR,
        DISCONNECTED
    }
}