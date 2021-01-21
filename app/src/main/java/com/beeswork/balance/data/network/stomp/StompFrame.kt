package com.beeswork.balance.data.network.stomp

import com.beeswork.balance.internal.provider.GsonProvider
import com.beeswork.balance.internal.safeLet
import org.threeten.bp.OffsetDateTime
import java.io.StringReader
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

data class StompFrame(
    val command: Command,
    val headers: Map<String, String>?,
    val message: Message?,
    val exception: Exception?
) {

    constructor(
        command: Command,
        headers: Map<String, String>,
        message: Message
    ) : this(command, headers, message, null)

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

    fun getMessageId(): Long? {
        return headers?.let {
            it[StompHeader.MESSAGE_ID]?.toLongOrNull()
        }
    }

    fun getError(): String? {
        return headers?.let {
            it[StompHeader.ERROR]
        }
    }

    fun getErrorMessage(): String? {
        return headers?.let {
            it[StompHeader.MESSAGE]
        }
    }

    companion object {
        const val TERMINATE_MESSAGE_SYMBOL = "\u0000"
        private const val HEADER_PATTERN = "([^:\\s]+)\\s*:\\s*([^\\n]+)"

        fun from(data: String?): StompFrame {
            if (data == null || data.trim().isEmpty())
                return StompFrame(Command.UNKNOWN, null)

            val reader = Scanner(StringReader(data))
            reader.useDelimiter(System.lineSeparator())
            val command = Command.valueOfDefault(reader.next())
            val headers = mutableMapOf<String, String>()

            val pattern = Pattern.compile(HEADER_PATTERN)
            while (reader.hasNext(pattern)) {
                val matcher: Matcher = pattern.matcher(reader.next())
                if (matcher.find()) {
                    safeLet(matcher.group(1), matcher.group(2)) { key, value ->
                        headers[key] = value
                    }
                }
            }
            reader.skip(System.lineSeparator() + System.lineSeparator())
            reader.useDelimiter(TERMINATE_MESSAGE_SYMBOL)

            val payload = if (reader.hasNext()) reader.next() else null
            payload?.let {
                when (command) {
                    Command.MESSAGE, Command.RECEIPT -> return StompFrame(
                        command,
                        headers,
                        GsonProvider.gson.fromJson(it, Message::class.java),
                        null
                    )
                    else -> println("")
                }
            }
            return StompFrame(command, headers)
        }
    }

    class Message(
        val id: Long?,
        val body: String,
        val accountId: String,
        val recipientId: String,
        val chatId: Long?,
        val createdAt: OffsetDateTime?
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
        UNKNOWN,
        MESSAGE,
        CONNECTED,
        RECEIPT,
        ERROR;

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