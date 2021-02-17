package com.beeswork.balance.data.database.dao

import androidx.room.*
import com.beeswork.balance.data.database.entity.Click
import java.util.*

@Dao
interface ClickDAO {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(click: Click)


    @Query("select swipedId from click")
    fun getSwipedIds(): List<UUID>

//  TODO: remove me
    @Query("select * from click")
    fun get(): List<Click>

}