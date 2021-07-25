package com.beeswork.balance.data.database.entity

import androidx.room.PrimaryKey
import com.beeswork.balance.internal.constant.LoginType
import java.util.*

data class Email(
    @PrimaryKey
    val accountId: UUID,

    val loginType: LoginType,
    val email: String? = null,
    val synced: Boolean = true,
)