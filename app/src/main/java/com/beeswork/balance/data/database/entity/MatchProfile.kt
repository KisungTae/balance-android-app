package com.beeswork.balance.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.beeswork.balance.data.network.response.Resource


@Entity(tableName = "matchProfile")
data class MatchProfile(

    @PrimaryKey
    val id: Int = ID,

    var lastMatchRowId: Int = 0,
    var lastUnmatchRowId: Int = Int.MIN_VALUE,
    var fetchMatchesStatus: Resource.Status = Resource.Status.SUCCESS
) {
    companion object {
        const val ID = 0
    }
}