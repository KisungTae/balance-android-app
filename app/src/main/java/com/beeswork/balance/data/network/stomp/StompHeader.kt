package com.beeswork.balance.data.network.stomp

class StompHeader {

    companion object {
        const val VERSION = "accept-version"
        const val DESTINATION = "destination"
        const val SUBSCRIPTION = "subscription"
        const val CONTENT_TYPE = "content-type"
        const val MESSAGE_ID = "message-id"
        const val ID = "id"
        const val ACK = "ack"
    }
}