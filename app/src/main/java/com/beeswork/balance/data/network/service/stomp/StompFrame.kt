package com.beeswork.balance.data.network.service.stomp

import com.beeswork.balance.internal.constant.PushType
import com.beeswork.balance.internal.constant.StompHeader
import com.beeswork.balance.internal.util.Converter
import com.beeswork.balance.internal.util.safeLet
import java.io.StringReader
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

data class StompFrame(
    val command: Command,
    val headers: Map<String, String>?,
    val payload: String?
) {
    fun compile(): String {
        val builder = StringBuilder()
        builder.append(command).append(System.lineSeparator())
        headers?.let {
            for ((k, v) in headers)
                builder.append(k).append(':').append(v).append(System.lineSeparator())
        }

        builder.append(System.lineSeparator())
        payload?.let {
            builder.append(it)
            builder.append(System.lineSeparator() + System.lineSeparator())
        }

        builder.append(TERMINATE_MESSAGE_SYMBOL)
        return builder.toString()
    }

    fun getPushType(): PushType? {
        return headers?.let { headers ->
            headers[StompHeader.PUSH_TYPE]?.let { pushType ->
                PushType.valueOf(pushType)
            }
        }
    }

    fun getMessageId(): Long? {
        return headers?.let {
            it[StompHeader.MESSAGE_ID]?.toLongOrNull()
        }
    }

    fun getReceiptId(): UUID? {
        return Converter.toUUID(headers?.get(StompHeader.RECEIPT_ID))
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
                return StompFrame(Command.UNKNOWN, null, null)

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
            return StompFrame(command, headers, payload)
        }
    }

    enum class Command {
        SEND,
        CONNECT,
        SUBSCRIBE,
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