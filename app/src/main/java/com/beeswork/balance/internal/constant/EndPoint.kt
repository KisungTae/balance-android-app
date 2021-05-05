package com.beeswork.balance.internal.constant

import java.util.*

class EndPoint {

    companion object {
//        const val PHOTO_BUCKET = "https://balance-photo-bucket.s3.ap-northeast-2.amazonaws.com"
        const val PHOTO_BUCKET = "https://test-balance-photo-bucket.s3-ap-southeast-2.amazonaws.com"
//        const val WEB_SOCKET_ENDPOINT = "ws://10.0.2.2:8080/chat"
//        const val WEB_SOCKET_ENDPOINT = "ws://192.168.1.100:8081/chat"
        const val WEB_SOCKET_ENDPOINT = "ws://192.168.1.92:8081/chat"
        const val STOMP_SEND_ENDPOINT = "/app/chat/send"
//        const val ACCOUNT_SERVICE_ENDPOINT = "http://192.168.1.100:8081/"
        const val ACCOUNT_SERVICE_ENDPOINT = "http://192.168.1.92:8081/"

        fun ofPhotoBucket(accountId: UUID, photoKey: String): String {
//            return "$PHOTO_BUCKET/$accountId/$photoKey"
            return "$PHOTO_BUCKET/$photoKey"
        }

        fun ofAccountProfilePhoto() {

        }
    }


}


// TODO: if you create new websocket while there is a connection, then it will be two connection