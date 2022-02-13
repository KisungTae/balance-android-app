package com.beeswork.balance.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.card.CardFilter
import java.util.*

@Dao
interface CardFilterDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(cardFilter: CardFilter)

    @Query("select * from cardFilter where accountId = :accountId")
    fun findById(accountId: UUID?): CardFilter

    @Query("update cardFilter set pageIndex = :pageIndex where accountId = :accountId")
    fun updatePageIndex(accountId: UUID?, pageIndex: Int)

    @Query("select count() > 0 from cardFilter where accountId = :accountId")
    fun existByAccountId(accountId: UUID?): Boolean

    @Query("delete from cardFilter where accountId = :accountId")
    fun deleteAll(accountId: UUID?)

    @Query("update cardFilter set gender = :gender, minAge = :minAge, maxAge = :maxAge, distance = :distance where accountId = :accountId")
    fun update(accountId: UUID?, gender: Boolean, minAge: Int, maxAge: Int, distance: Int)
}