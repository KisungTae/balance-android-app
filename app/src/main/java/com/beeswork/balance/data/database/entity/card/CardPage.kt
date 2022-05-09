package com.beeswork.balance.data.database.entity.card

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "cardPage")
data class CardPage(

    @PrimaryKey
    val accountId: UUID,

    var currentIndex: Int
)