package com.beeswork.balance.internal.constant

import java.util.*

class EndPoint {

    companion object {

//        private const val BALANCE_WEB_URL = "192.168.1.119:8081"
        private const val WEB_SERVICE_URL = "10.0.2.2:8080"
        const val WEB_SERVICE_ENDPOINT = "http://$WEB_SERVICE_URL/"
        const val WEB_SOCKET_ENDPOINT = "ws://$WEB_SERVICE_URL/web-socket"
        const val STOMP_SEND_ENDPOINT = "/app/chat/send"

        fun ofPhoto(photoBucketURL: String?, accountId: UUID?, photoKey: String?): String {
            return "$photoBucketURL${Delimiter.FORWARD_SLASH}$accountId${Delimiter.FORWARD_SLASH}$photoKey"
        }
    }


}


// TODO: if you create new websocket while there is a connection, then it will be two connection