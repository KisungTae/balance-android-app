package com.beeswork.balance.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.card.CardFilter
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface CardFilterDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(cardFilter: CardFilter)

    @Query("select * from cardFilter where accountId = :accountId")
    fun getBy(accountId: UUID?): CardFilter?

    @Query("update cardFilter set pageIndex = :pageIndex where accountId = :accountId")
    fun updatePageIndexBy(accountId: UUID?, pageIndex: Int)

    @Query("select count() > 0 from cardFilter where accountId = :accountId")
    fun existBy(accountId: UUID?): Boolean

    @Query("delete from cardFilter where accountId = :accountId")
    fun deleteAllBy(accountId: UUID?)

    @Query("update cardFilter set gender = :gender, minAge = :minAge, maxAge = :maxAge, distance = :distance where accountId = :accountId")
    fun updateBy(accountId: UUID?, gender: Boolean, minAge: Int, maxAge: Int, distance: Int)

    @Query("select gender from cardFilter where accountId = :accountId")
    fun getCardFilterGenderFlow(accountId: UUID?): Flow<Boolean?>
}