package com.beeswork.balance.internal.provider.gson

import com.google.gson.GsonBuilder
import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime

object GsonProvider {

    val gson = GsonBuilder().registerTypeAdapter(
        OffsetDateTime::class.java,
        OffsetDateTimeToISOStringSerializer()
    ).registerTypeAdapter(
        OffsetDateTime::class.java,
        StringToOffsetDateTimeDeserializer()
    ).registerTypeAdapter(
        LocalDate::class.java,
        StringToLocalDateDeserializer()
    ).create()


}