package com.beeswork.balance.data.database.dao

import androidx.room.*
import com.beeswork.balance.data.database.entity.Click

@Dao
interface ClickDAO {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(click: Click)

    @Query("update click set posted = :posted where swipeId = :swipeId")
    fun update(swipeId: Long, posted: Boolean)

    @Query("delete from click where swipeId = :swipeId")
    fun delete(swipeId: Long)

    @Query("select swipedId from click")
    fun getSwipedIds(): List<String>

    @Query("delete from click where swipedId = :swipedId")
    fun deleteBySwipedId(swipedId: String)
}