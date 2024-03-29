package com.beeswork.balance.data.database.entity.setting

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fcmToken")
data class FCMToken(

    val token: String,

    @PrimaryKey
    val id: Int = ID
) {
    companion object {
        const val ID = 0
    }
}