package com.beeswork.balance.data.observable

class WebSocketEvent(
    val type: Type,
    val error: String?,
    val errorMessage: String?
) {

    companion object {
        fun error(error: String?, errorMessage: String?): WebSocketEvent {
            return WebSocketEvent(Type.ERROR, error, errorMessage)
        }

        fun disconnect(): WebSocketEvent {
            return WebSocketEvent(Type.DISCONNECTED, null, null)
        }
    }


    enum class Type {
        CONNECTED,
        ERROR,
        DISCONNECTED
    }
}