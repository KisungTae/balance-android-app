package com.beeswork.balance.data.database.entity.tabcount

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.beeswork.balance.internal.constant.TabPosition
import org.threeten.bp.OffsetDateTime
import java.util.*


@Entity(tableName = "tabCount")
data class TabCount(

    @PrimaryKey
    val accountId: UUID,

    val tabPosition: TabPosition,
    var count: Long,
    var countedAt: OffsetDateTime
)