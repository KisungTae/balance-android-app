package com.beeswork.balance.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime


@Entity(tableName = "profile")
data class Profile(
    val name: String,
    val birth: OffsetDateTime,
    val gender: Boolean,
    val height: Int?,
    val about: String,

    @PrimaryKey
    val id: Int = ID,
) {
    companion object {
        const val ID = 0
    }
}