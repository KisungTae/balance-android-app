package com.beeswork.balance.data.network.api

import com.beeswork.balance.internal.converter.StringToOffsetDateTimeDeserializer
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import org.threeten.bp.OffsetDateTime
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface S3Api {

    companion object {
        operator fun invoke(
            okHttpClient: OkHttpClient
        ): S3Api {

            val gson = GsonBuilder().registerTypeAdapter(
                OffsetDateTime::class.java,
                StringToOffsetDateTimeDeserializer()
            ).create()

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("")
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(S3Api::class.java)
        }
    }
}