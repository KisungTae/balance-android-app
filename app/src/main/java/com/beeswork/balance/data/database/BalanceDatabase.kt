package com.beeswork.balance.data.database

import android.content.Context
import androidx.room.*
import com.beeswork.balance.data.dao.*
import com.beeswork.balance.data.entity.*
import com.beeswork.balance.internal.converter.OffsetDateTimeConverter


@Database(
    entities = [Match::class, Message::class, Click::class, FCMToken::class, Clicked::class],
    version = 1
)
@TypeConverters(OffsetDateTimeConverter::class)
abstract class BalanceDatabase : RoomDatabase() {

    abstract fun matchDAO(): MatchDAO
    abstract fun messageDAO(): MessageDAO
    abstract fun clickDAO(): ClickDAO
    abstract fun fcmTokenDAO(): FCMTokenDAO
    abstract fun clickedDAO(): ClickedDAO

    companion object {

        @Volatile
        private var instance: BalanceDatabase? = null
        private val lock = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(lock = lock) {
            instance ?: build(context).also { instance = it }
        }

        private fun build(context: Context) = Room.databaseBuilder(context.applicationContext,
                                                                   BalanceDatabase::class.java,
                                                                   "balance.db").build()

    }
}