package com.beeswork.balance.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clicked")
data class Clicked(

    @PrimaryKey
    val swipedId: String,

    val photoKey: String
)