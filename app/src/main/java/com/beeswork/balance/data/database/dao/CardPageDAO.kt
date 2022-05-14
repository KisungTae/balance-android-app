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

    @Query("update cardPage set currentIndex = :currentIndex, readByIndex = :readyByIndex where accountId = :accountId")
    fun updateBy(accountId: UUID?, currentIndex: Int, readyByIndex: Int)

    @Query("select * from cardPage where accountId = :accountId")
    fun getBy(accountId: UUID?): CardPage?

    @Query("update cardPage set readByIndex = readByIndex + :incrementBy where accountId = :accountId")
    fun incrementReadByIndexBy(accountId: UUID?, incrementBy: Int)
}