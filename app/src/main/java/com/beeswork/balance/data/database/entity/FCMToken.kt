package com.beeswork.balance.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fcmToken")
data class FCMToken(

    val token: String,
    val posted: Boolean,

    @PrimaryKey
    val id: Int = ID
) {
    companion object {
        const val ID = 0
    }
}