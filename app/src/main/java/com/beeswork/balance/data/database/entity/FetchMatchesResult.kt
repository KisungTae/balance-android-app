package com.beeswork.balance.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.beeswork.balance.data.network.response.Resource
import org.threeten.bp.OffsetDateTime

@Entity(tableName = "fetchMatchesResult")
data class FetchMatchesResult(

    @PrimaryKey
    val id: Int = ID,

    var status: Resource.Status = Resource.Status.SUCCESS,
    var chatMessagesInsertedAt: OffsetDateTime = OffsetDateTime.now(),
) {
    companion object {
        const val ID = 0
    }
}