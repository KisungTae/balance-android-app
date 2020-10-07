package com.beeswork.balance.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.beeswork.balance.internal.constant.FirebaseMessagingTokenConstant
import org.threeten.bp.OffsetDateTime


@Entity(tableName = "firebaseMessagingToken")
data class FirebaseMessagingToken(

    val token: String,
    val posted: Boolean,
    val createdAt: OffsetDateTime,

    @PrimaryKey
    val id: Int = FirebaseMessagingTokenConstant.id,

    )