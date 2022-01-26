package com.beeswork.balance.data.network.service.stomp

class WebSocketEvent(
    val status: Status,
    val exception: Throwable?
) {

    fun isError(): Boolean {
        return status == Status.ERROR
    }

    companion object {
        fun error(exception: Throwable?): WebSocketEvent {
            return WebSocketEvent(Status.ERROR, exception)
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