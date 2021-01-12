package com.beeswork.balance.internal.converter

import com.google.gson.*
import org.threeten.bp.OffsetDateTime
import java.lang.reflect.Type


class OffsetDateTimeToISOStringSerializer: JsonSerializer<OffsetDateTime> {

    override fun serialize(
        src: OffsetDateTime?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement? {
        return try {
            JsonPrimitive(src.toString())
        } catch (e: Exception) {
            null
        }
    }

}
