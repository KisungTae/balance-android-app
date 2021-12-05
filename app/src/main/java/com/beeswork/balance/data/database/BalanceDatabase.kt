package com.beeswork.balance.data.database

import android.content.Context
import androidx.room.*
import com.beeswork.balance.data.database.converter.*
import com.beeswork.balance.data.database.dao.*
import com.beeswork.balance.data.database.entity.*
import com.beeswork.balance.data.database.entity.chat.ChatMessage
import com.beeswork.balance.data.database.entity.click.Click
import com.beeswork.balance.data.database.entity.login.Login
import com.beeswork.balance.data.database.entity.match.Match
import com.beeswork.balance.data.database.entity.photo.Photo
import com.beeswork.balance.data.database.entity.profile.Profile
import com.beeswork.balance.data.database.entity.setting.FCMToken
import com.beeswork.balance.data.database.entity.FetchInfo
import com.beeswork.balance.data.database.entity.setting.Location
import com.beeswork.balance.data.database.entity.setting.PushSetting
import com.beeswork.balance.data.database.entity.swipe.SwipeFilter


@Database(
    entities = [Match::class, ChatMessage::class, Swipe::class, FCMToken::class,
        Click::class, Profile::class, Location::class, Photo::class, SwipeFilter::class, PushSetting::class, FetchInfo::class, Login::class],
    version = 1
)
@TypeConverters(
    OffsetDateTimeConverter::class,
    ChatMessageStatusConverter::class,
    UUIDConverter::class,
    ResourceStatusConverter::class,
    PhotoStatusConverter::class,
    UriConverter::class,
    LoginTypeConverter::class
)
abstract class BalanceDatabase : RoomDatabase() {

    abstract fun matchDAO(): MatchDAO
    abstract fun chatMessageDAO(): ChatMessageDAO
    abstract fun swipeDAO(): SwipeDAO
    abstract fun fcmTokenDAO(): FCMTokenDAO
    abstract fun clickDAO(): ClickDAO
    abstract fun profileDAO(): ProfileDAO
    abstract fun locationDAO(): LocationDAO
    abstract fun photoDAO(): PhotoDAO
    abstract fun swipeFilterDAO(): SwipeFilterDAO
    abstract fun settingDAO(): PushSettingDAO
    abstract fun fetchInfoDAO(): FetchInfoDAO
    abstract fun loginDAO(): LoginDAO

    companion object {

        @Volatile
        private var instance: BalanceDatabase? = null
        private val lock = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(lock = lock) {
            instance ?: build(context).also { instance = it }
        }

        private fun build(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            BalanceDatabase::class.java,
            "balance.db"
        ).build()

    }
}


// list all indexes
// SELECT name, tbl_name  FROM sqlite_master  WHERE type = 'index';
