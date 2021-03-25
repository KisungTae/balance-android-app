package com.beeswork.balance.data.database.entity

import androidx.room.PrimaryKey

data class ChatMessageProfile(
    val lastMessageId: Long = Long.MAX_VALUE,

//    @PrimaryKey
    val id: Int = ID
) {
    companion object {
        const val ID = 0
    }
}