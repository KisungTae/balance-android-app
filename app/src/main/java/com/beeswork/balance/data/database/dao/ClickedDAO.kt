package com.beeswork.balance.data.database.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.Clicked


@Dao
interface ClickedDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(clickedList: List<Clicked>)

    @Query("select * from clicked order by updatedAt desc")
    fun getClickedList(): DataSource.Factory<Int, Clicked>

    @Query("delete from clicked where swiperId = :swiperId")
    fun deleteBySwiperId(swiperId: String)

    @Query("delete from clicked where swiperId in (select matchedId from `match`)")
    fun deleteIfMatched()

    @Query("select count(*) from clicked")
    fun count(): LiveData<Int>

    @Query("select * from clicked")
    fun get(): List<Clicked>
}