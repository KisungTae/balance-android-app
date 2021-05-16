package com.beeswork.balance.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.Location
import com.beeswork.balance.data.database.entity.SwipeFilter

@Dao
interface SwipeFilterDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(swipeFilter: SwipeFilter)

    @Query("select * from swipeFilter where id = ${SwipeFilter.ID}")
    fun findById(): SwipeFilter?

    @Query("update swipeFilter set pageIndex = :pageIndex where id = ${SwipeFilter.ID}")
    fun updatePageIndex(pageIndex: Int)
}