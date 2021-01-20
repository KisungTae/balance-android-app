package com.beeswork.balance.data.network.stomp

import com.google.gson.JsonPrimitive

class WebSocketLifeCycleEvent(
    val type: Type,
    val error: String?,
    val errorMessage: String?
) {



    companion object {
        fun error(error: String?, errorMessage: String?): WebSocketLifeCycleEvent {
            return WebSocketLifeCycleEvent(Type.ERROR, error, errorMessage)
        }

        fun disconnect(): WebSocketLifeCycleEvent {
            return WebSocketLifeCycleEvent(Type.DISCONNECTED, null, null)
        }
    }


    enum class Type {
        CONNECTED,
        ERROR,
        DISCONNECTED
    }
}