package com.beeswork.balance.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.internal.util.DateHelper
import org.threeten.bp.OffsetDateTime
import java.util.*

@Entity(tableName = "fetchInfo")
data class FetchInfo(

    @PrimaryKey
    val accountId: UUID,

    val clickFetchedAt: OffsetDateTime = defaultOffsetDateTime,
    val matchFetchedAt: OffsetDateTime = defaultOffsetDateTime,
    val fetchChatMessagesStatus: Resource.Status = Resource.Status.SUCCESS
) {
    companion object {
        val defaultOffsetDateTime: OffsetDateTime = OffsetDateTime.parse("2000-01-01T10:00:00.232+00:00")
    }
}