package com.beeswork.balance.data.database.entity.card

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.beeswork.balance.internal.constant.Gender
import java.util.*
import kotlin.math.max

@Entity(tableName = "cardFilter")
data class CardFilter(
    @PrimaryKey
    val accountId: UUID,

    var gender: Boolean?,
    var minAge: Int,
    var maxAge: Int,
    var distance: Int,
    var pageIndex: Int = 0
) {
    fun isEqualTo(gender: Boolean, minAge: Int, maxAge: Int, distance: Int): Boolean {
        return this.gender == gender && this.minAge == minAge && this.maxAge == maxAge && this.distance == distance
    }
}