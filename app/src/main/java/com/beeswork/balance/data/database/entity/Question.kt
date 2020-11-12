package com.beeswork.balance.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "question")
data class Question(

    val bottom

    @PrimaryKey
    val id: Int
)