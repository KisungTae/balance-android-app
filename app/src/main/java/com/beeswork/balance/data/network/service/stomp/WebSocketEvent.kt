package com.beeswork.balance.data.network.service.stomp

class WebSocketEvent(
    val type: Type,
    val error: String?,
    val errorMessage: String?
) {

    fun isError(): Boolean {
        return type == Type.ERROR
    }

    companion object {
        fun error(error: String?, errorMessage: String?): WebSocketEvent {
            return WebSocketEvent(Type.ERROR, error, errorMessage)
        }

        fun disconnect(): WebSocketEvent {
            return WebSocketEvent(Type.CLOSED, null, null)
        }
    }


    enum class Type {
        OPENED,
        ERROR,
        CLOSED
    }
}