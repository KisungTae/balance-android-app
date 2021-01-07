package com.beeswork.balance.data.network.stomp

class StompFrame(
    private val command: Command,
    private val headers: Map<String, String>,
    private val payload: String?
) {


    fun compile(): String {
        val builder = StringBuilder()
        builder.append(command).append(System.lineSeparator())
        for ((k, v) in headers)
            builder.append(k).append(':').append(v).append(System.lineSeparator())
        builder.append(System.lineSeparator())
        payload?.let {
            builder.append(payload)
            builder.append(System.lineSeparator() + System.lineSeparator())
        }
        builder.append(TERMINATE_MESSAGE_SYMBOL)
        return builder.toString()
    }

    companion object {
        const val TERMINATE_MESSAGE_SYMBOL = "\u0000"

    }

    enum class Command {
        SEND,
        SUBSCRIBE,
        UNSUBSCRIBE,
        BEGIN,
        COMMIT,
        ABORT,
        ACK,
        NACK,
        DISCONNECT
    }


}