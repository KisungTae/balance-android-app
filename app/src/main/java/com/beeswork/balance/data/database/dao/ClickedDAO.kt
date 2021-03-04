package com.beeswork.balance.data.database.dao

import androidx.room.*
import com.beeswork.balance.data.database.entity.Clicked
import java.util.*

@Dao
interface ClickedDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(clicked: Clicked)

    @Query("select id from clicked")
    fun getClickedIds(): List<UUID>

//  TODO: remove me
    @Query("select * from clicked")
    fun get(): List<Clicked>

}