package com.beeswork.balance.data.network.stomp

import com.beeswork.balance.data.database.entity.Message
import com.google.gson.Gson
import java.io.StringReader
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class StompFrame(
    private val command: Command,
    private val headers: Map<String, String>?,
    private val message: Message?
) {

    constructor(
        command: String,
        headers: Map<String, String>?,
        message: Message?
    ) : this(Command.valueOfDefault(command), headers, message)



    fun compile(): String {
        val builder = StringBuilder()
        builder.append(command).append(System.lineSeparator())
        headers?.let {
            for ((k, v) in headers)
                builder.append(k).append(':').append(v).append(System.lineSeparator())
        }

        builder.append(System.lineSeparator())
        message?.let {
            Gson().toJson(message)
//            builder.append(payload)
            builder.append(System.lineSeparator() + System.lineSeparator())
        }

        builder.append(TERMINATE_MESSAGE_SYMBOL)
        return builder.toString()
    }

    companion object {
        const val TERMINATE_MESSAGE_SYMBOL = "\u0000"
        private const val HEADER_PATTERN = "([^:\\s]+)\\s*:\\s*([^\\n]+)"

        fun from(data: String?): StompFrame {
//            if (data == null || data.trim().isEmpty())
//                return StompFrame(Command.UNKNOWN, null, data)

            val reader = Scanner(StringReader(data))
            reader.useDelimiter(System.lineSeparator())
            val command = reader.next()
            val headers = mutableMapOf<String, String>()

            val pattern = Pattern.compile(HEADER_PATTERN)
            while (reader.hasNext(pattern)) {
                val matcher: Matcher = pattern.matcher(reader.next())
                if (matcher.find()) {
                    matcher.group(1)?.let { key ->
                        matcher.group(2)?.let { value ->
                            headers[key] = value
                        }
                    }
                }
            }
            reader.skip(System.lineSeparator() + System.lineSeparator())
            reader.useDelimiter(TERMINATE_MESSAGE_SYMBOL)
            val payload = if (reader.hasNext()) reader.next() else null
            payload?.let {

            }
            // TODO: modify to return message in stompFrame
            return StompFrame(command, headers, null)
        }
    }

    enum class Command {
        SEND,
        CONNECT,
        SUBSCRIBE,
        UNSUBSCRIBE,
        BEGIN,
        COMMIT,
        ABORT,
        ACK,
        NACK,
        DISCONNECT,
        UNKNOWN;

        companion object {
            fun valueOfDefault(command: String): Command {
                return try {
                    valueOf(command)
                } catch (e: IllegalArgumentException) {
                    UNKNOWN
                }
            }
        }

    }


}