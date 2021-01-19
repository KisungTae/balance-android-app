package com.beeswork.balance.data.network.stomp

class StompHeader {
    companion object {
        const val VERSION = "accept-version"
        const val HEART_BEAT = "heart-beat"
        const val DESTINATION = "destination"
        const val SUBSCRIPTION = "subscription"
        const val CONTENT_TYPE = "content-type"
        const val MESSAGE_ID = "message-id"
        const val ID = "id"
        const val ACK = "ack"
        const val AUTO_DELETE = "auto-delete"
        const val EXCLUSIVE = "exclusive"
        const val DURABLE = "durable"
        const val CHAT_ID = "chat-id"
        const val RECIPIENT_ID = "recipient-id"
        const val ACCOUNT_ID = "account-id"
        const val IDENTITY_TOKEN = "identity-token"
        const val RECEIPT = "receipt"
    }
}