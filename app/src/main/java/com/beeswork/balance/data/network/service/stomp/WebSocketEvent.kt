package com.beeswork.balance.data.network.service.stomp

class WebSocketEvent(
    val status: Status,
    val throwable: Throwable?
) {

    fun isError(): Boolean {
        return status == Status.ERROR
    }

    companion object {
        fun error(throwable: Throwable?): WebSocketEvent {
            return WebSocketEvent(Status.ERROR, throwable)
        }

        fun disconnect(): WebSocketEvent {
            return WebSocketEvent(Status.CLOSED, null)
        }
    }


    enum class Status {
        OPENED,
        ERROR,
        CLOSED
    }
}