package com.beeswork.balance.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.beeswork.balance.internal.constant.LoginType
import java.util.*

@Entity(tableName = "login")
data class Login(

    @PrimaryKey
    val accountId: UUID,

    val type: LoginType,
    val email: String? = null,
    val synced: Boolean = true,
)