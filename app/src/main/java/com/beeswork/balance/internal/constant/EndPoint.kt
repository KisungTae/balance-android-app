package com.beeswork.balance.internal.constant

import com.beeswork.balance.internal.util.safeLet
import java.util.*

class EndPoint {

    companion object {
//        const val PHOTO_BUCKET = "https://balance-photo-bucket.s3.ap-northeast-2.amazonaws.com"
        private const val PHOTO_BUCKET = "https://test-balance-photo-bucket.s3-ap-southeast-2.amazonaws.com"
//        private const val PHOTO_BUCKET = "https://test-balance-photo-bucket.s3-ap-southeast-2.amazonaw.com"
//        const val WEB_SOCKET_ENDPOINT = "ws://10.0.2.2:8080/chat"
        const val WEB_SOCKET_ENDPOINT = "ws://192.168.1.100:8081/chat"
//        const val WEB_SOCKET_ENDPOINT = "ws://192.168.1.92:8081/chat"
        const val STOMP_SEND_ENDPOINT = "/app/chat/send"

        const val ACCOUNT_SERVICE_ENDPOINT = "http://10.0.2.2:8080/"
//        const val ACCOUNT_SERVICE_ENDPOINT = "http://192.168.1.100:8081/"
//        const val ACCOUNT_SERVICE_ENDPOINT = "http://192.168.1.92:8081/"

        fun ofPhoto(accountId: UUID?, photoKey: String?): String? {
            return safeLet(accountId, photoKey) { id, key ->
                return@safeLet "$PHOTO_BUCKET/$id/$key"
            }
        }

        fun ofAccountProfilePhoto() {

        }
    }


}


// TODO: if you create new websocket while there is a connection, then it will be two connection