package com.beeswork.balance.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.card.CardPage
import java.util.*

@Dao
interface CardPageDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(cardPage: CardPage)

    @Query("update cardPage set currentIndex = :pageIndex where accountId = :accountId")
    fun updateCurrentIndex(accountId: UUID?, pageIndex: Int)

    @Query("select currentIndex from cardPage where accountId = :accountId")
    fun getCurrentIndexBy(accountId: UUID?): Int?
}