package com.beeswork.balance.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.Click
import com.beeswork.balance.data.database.entity.FetchInfo
import org.threeten.bp.OffsetDateTime
import java.util.*

@Dao
interface FetchInfoDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(fetchInfo: FetchInfo)

    @Query("update fetchInfo set clickFetchedAt = :clickFetchedAt where accountId = :accountId")
    fun updateClickFetchedAt(accountId: UUID?, clickFetchedAt: OffsetDateTime)

    @Query("update fetchInfo set matchFetchedAt = :matchFetchedAt where accountId = :accountId")
    fun updateMatchFetchedAt(accountId: UUID?, matchFetchedAt: OffsetDateTime)

    @Query("select clickFetchedAt from fetchInfo where accountId = :accountId")
    fun findClickFetchedAt(accountId: UUID?): OffsetDateTime

    @Query("select matchFetchedAt from fetchInfo where accountId = :accountId")
    fun findMatchFetchedAt(accountId: UUID?): OffsetDateTime

    @Query("select count(*) > 0 from fetchInfo where accountId =:accountId")
    fun existByAccountId(accountId: UUID?): Boolean
}