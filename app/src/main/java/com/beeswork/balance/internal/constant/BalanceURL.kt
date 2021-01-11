package com.beeswork.balance.internal.constant

class BalanceURL {

    companion object {
        const val PHOTO_BUCKET = "https://balance-photo-bucket.s3.ap-northeast-2.amazonaws.com"
        const val WEB_SOCKET_ENDPOINT = "ws://10.0.2.2:8080/chat/websocket"
        const val STOMP_SEND_ENDPOINT = "/app/chat/send"
    }
}