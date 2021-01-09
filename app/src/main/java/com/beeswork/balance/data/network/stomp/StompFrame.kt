package com.beeswork.balance.data.network.stomp

import java.io.StringReader
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class StompFrame(
    private val command: Command,
    private val headers: Map<String, String>?,
    private val payload: String?
) {

    constructor(
        command: String,
        headers: Map<String, String>?,
        payload: String?
    ) : this(Command.valueOfDefault(command), headers, payload)



    fun compile(): String {
        val builder = StringBuilder()
        builder.append(command).append(System.lineSeparator())
        headers?.let {
            for ((k, v) in headers)
                builder.append(k).append(':').append(v).append(System.lineSeparator())
        }

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
        private const val HEADER_PATTERN = "([^:\\s]+)\\s*:\\s*([^\\n]+)"

        fun from(data: String?): StompFrame {
            if (data == null || data.trim().isEmpty())
                return StompFrame(Command.UNKNOWN, null, data)

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
            return StompFrame(command, headers, payload)
        }
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