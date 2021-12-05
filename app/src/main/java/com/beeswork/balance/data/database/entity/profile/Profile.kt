package com.beeswork.balance.data.database.entity.profile

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime
import java.util.*


@Entity(tableName = "profile")
data class Profile(

    @PrimaryKey
    val accountId: UUID,

    val name: String,
    val birth: OffsetDateTime,
    val gender: Boolean,
    val height: Int?,
    val about: String,
    var synced: Boolean
)