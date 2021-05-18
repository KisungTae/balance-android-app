package com.beeswork.balance.internal.constant

import com.beeswork.balance.internal.util.safeLet
import com.google.gson.*
import com.google.gson.annotations.JsonAdapter
import java.lang.reflect.Type


@JsonAdapter(ReportReason.Serializer::class)
enum class ReportReason {
    NOTHING,
    MESSAGE,
    PHOTO,
    SPAM,
    BEHAVIOUR,
    OTHER;

    internal class Serializer : JsonSerializer<ReportReason?> {
        override fun serialize(src: ReportReason?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement? {
            return safeLet(src, context) { s, c ->
                c.serialize(s.ordinal)
            }
        }
    }
}