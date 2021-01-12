package com.beeswork.balance.data.network.stomp

import com.beeswork.balance.data.database.entity.Message
import com.beeswork.balance.internal.converter.OffsetDateTimeToISOStringSerializer
import com.beeswork.balance.internal.converter.StringToOffsetDateTimeDeserializer
import com.beeswork.balance.internal.provider.GsonProvider
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.threeten.bp.OffsetDateTime
import java.io.StringReader
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

data class StompFrame(
    private val command: Command,
    private val headers: Map<String, String>?,
    private val message: Message?,
    private val exception: Exception?
) {

    constructor(
        command: Command,
        headers: Map<String, String>,
        message: String,
        createdAt: OffsetDateTime
    ) : this(command, headers, Message(message, createdAt), null)

    constructor(
        command: Command,
        headers: Map<String, String>?
    ) : this(command, headers, null, null)


    fun compile(): String {
        val builder = StringBuilder()
        builder.append(command).append(System.lineSeparator())
        headers?.let {
            for ((k, v) in headers)
                builder.append(k).append(':').append(v).append(System.lineSeparator())
        }

        builder.append(System.lineSeparator())
        message?.let {
            builder.append(GsonProvider.gson.toJson(message))
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
            return StompFrame(Command.valueOfDefault(command), headers)
        }
    }

    class Message(
        val message: String,
        val createdAt: OffsetDateTime
    )

    class Exception(
        val error: String,
        val message: String
    )

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