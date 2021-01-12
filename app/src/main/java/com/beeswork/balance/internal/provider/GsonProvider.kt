package com.beeswork.balance.internal.provider

import com.beeswork.balance.internal.converter.OffsetDateTimeToISOStringSerializer
import com.beeswork.balance.internal.converter.StringToOffsetDateTimeDeserializer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.threeten.bp.OffsetDateTime

object GsonProvider {

    val gson = GsonBuilder().registerTypeAdapter(
        OffsetDateTime::class.java,
        OffsetDateTimeToISOStringSerializer()
    ).registerTypeAdapter(
        OffsetDateTime::class.java,
        StringToOffsetDateTimeDeserializer()
    ).create()


}