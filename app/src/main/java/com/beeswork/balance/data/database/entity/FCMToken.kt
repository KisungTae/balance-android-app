package com.beeswork.balance.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.beeswork.balance.internal.constant.CURRENT_FCM_TOKEN_ID
import org.threeten.bp.OffsetDateTime




@Entity(tableName = "fcmToken")
data class FCMToken(

    val token: String,
    val posted: Boolean,
    val createdAt: OffsetDateTime,

    @PrimaryKey
    val id: Int = CURRENT_FCM_TOKEN_ID
)