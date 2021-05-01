package com.beeswork.balance.internal.constant

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

    internal class Serializer : JsonSerializer<ReportReason?>, JsonDeserializer<ReportReason?> {
        override fun serialize(src: ReportReason?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement? {
            return context?.serialize(src?.ordinal ?: NOTHING.ordinal)
        }

        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): ReportReason {
            return values()[json?.asInt ?: 0]
        }
    }
}