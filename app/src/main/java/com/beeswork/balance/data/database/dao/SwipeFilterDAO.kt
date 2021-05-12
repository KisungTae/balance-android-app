package com.beeswork.balance.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.beeswork.balance.data.database.entity.SwipeFilter

@Dao
interface SwipeFilterDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(swipeFilter: SwipeFilter)
}