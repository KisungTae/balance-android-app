package com.beeswork.balance.data.database.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import com.beeswork.balance.data.database.entity.Clicked


@Dao
interface ClickedDAO {

    @Query("select * from clicked order by updatedAt desc")
    fun getClicked(): DataSource.Factory<Int, Clicked>

    @Query("delete from clicked where swiperId = :swiperId")
    fun deleteBySwiperId(swiperId: String)
}