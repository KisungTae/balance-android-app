package com.beeswork.balance.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime

@Entity(tableName = "matchProfile")
data class MatchProfile(

    @PrimaryKey
    val id: Int = ID,

    var matchFetchedAt: OffsetDateTime = OffsetDateTime.MIN,
    var accountFetchedAt: OffsetDateTime = OffsetDateTime.MIN,
    var chatMessagesFetchedAt: OffsetDateTime = OffsetDateTime.MIN,
    var chatMessagesInsertedAt: OffsetDateTime = OffsetDateTime.now()


) {
    companion object {
        const val ID = 0
    }
}