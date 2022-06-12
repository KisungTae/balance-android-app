package com.beeswork.balance.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.tabcount.TabCount
import com.beeswork.balance.internal.constant.TabPosition
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface TabCountDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tabCount: TabCount)

    @Query("select * from tabCount where accountId = :accountId")
    fun getFlowBy(accountId: UUID?): Flow<List<TabCount>>

    @Query("select * from tabCount where accountId = :accountId and tabPosition = :tabPosition")
    fun getBy(accountId: UUID?, tabPosition: TabPosition): TabCount?
}
