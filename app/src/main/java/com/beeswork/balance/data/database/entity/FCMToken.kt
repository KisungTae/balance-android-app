package com.beeswork.balance.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

const val FCM_TOKEN_ID = 0

@Entity(tableName = "fcmToken")
data class FCMToken(

    val token: String,
    val posted: Boolean,

    @PrimaryKey
    val id: Int = FCM_TOKEN_ID
)