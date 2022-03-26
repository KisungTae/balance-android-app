package com.beeswork.balance.internal.exception

class WebSocketDisconnectedException: BaseException(CODE, null) {
    companion object {
        const val CODE = "web_socket_disconnected_exception"
    }
}