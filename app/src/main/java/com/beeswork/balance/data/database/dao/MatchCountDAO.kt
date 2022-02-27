package com.beeswork.balance.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.match.MatchCount
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface MatchCountDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(matchCount: MatchCount)

    @Query("select * from matchCount where accountId = :accountId")
    fun findBy(accountId: UUID?): MatchCount?

    @Query("select count from matchCount where accountId = :accountId")
    fun getCountFlow(accountId: UUID?): Flow<Long?>

    @Query("select count from matchCount where accountId = :accountId")
    fun getCountBy(accountId: UUID?): Long?

    @Query("delete from matchCount where accountId = :accountId")
    fun deleteBy(accountId: UUID?)
}