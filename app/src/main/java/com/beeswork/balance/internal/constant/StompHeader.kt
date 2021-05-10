package com.beeswork.balance.internal.constant

class StompHeader {
    companion object {
        const val VERSION = "accept-version"
        const val HEART_BEAT = "heart-beat"
        const val DESTINATION = "destination"
        const val MESSAGE_ID = "message-id"
        const val ACCOUNT_ID = "account-id"
        const val IDENTITY_TOKEN = "identity-token"
        const val RECEIPT = "receipt"
        const val ERROR = "error"
        const val MESSAGE = "message"
        const val ACCEPT_LANGUAGE = "accept-language"
        const val PUSH_TYPE = "pushType"
        const val RECEIPT_ID = "receipt-id"
        const val UNMATCHED_RECEIPT_ID = -1L
    }
}