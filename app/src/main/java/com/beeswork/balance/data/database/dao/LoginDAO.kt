package com.beeswork.balance.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.Login
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface LoginDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(login: Login)

    @Query("select email from login where accountId = :accountId")
    fun findEmailAsFlow(accountId: UUID): Flow<String?>

    @Query("select * from login where accountId = :accountId")
    fun findByAccountId(accountId: UUID): Login?

    @Query("update login set email = :email, synced = 1 where accountId = :accountId")
    fun updateEmail(accountId: UUID, email: String)

    @Query("update login set synced = :synced where accountId = :accountId")
    fun updateSynced(accountId: UUID, synced: Boolean)

    @Query("delete from login where accountId = :accountId")
    fun deleteByAccountId(accountId: UUID)

    @Query("select synced from login where accountId = :accountId")
    fun isSynced(accountId: UUID): Boolean?


}