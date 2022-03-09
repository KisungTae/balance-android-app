package com.beeswork.balance.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beeswork.balance.data.database.entity.login.Login
import com.beeswork.balance.internal.constant.LoginType
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface LoginDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(login: Login)

    @Query("select email from login where accountId = :accountId")
    fun getEmailFlowBy(accountId: UUID?): Flow<String?>

    @Query("select * from login where accountId = :accountId")
    fun getBy(accountId: UUID): Login?

    @Query("update login set email = :email, synced = 1 where accountId = :accountId")
    fun updateEmailBy(accountId: UUID, email: String?)

    @Query("update login set synced = :synced where accountId = :accountId")
    fun updateSyncedBy(accountId: UUID, synced: Boolean)

    @Query("delete from login where accountId = :accountId")
    fun deleteBy(accountId: UUID?)

    @Query("select synced from login where accountId = :accountId")
    fun isSyncedBy(accountId: UUID?): Boolean?

    @Query("select * from login where accountId = :accountId")
    fun getLoginFlowBy(accountId: UUID): Flow<Login>

    @Query("select email from login where accountId = :accountId")
    fun getEmailBy(accountId: UUID?): String?

    @Query("select type from login where accountId = :accountId")
    fun getLoginTypeBy(accountId: UUID?): LoginType?
}