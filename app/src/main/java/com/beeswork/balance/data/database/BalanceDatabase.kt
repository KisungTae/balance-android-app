package com.beeswork.balance.data.database

import android.content.Context
import androidx.room.*
import com.beeswork.balance.data.database.converter.*
import com.beeswork.balance.data.database.dao.*
import com.beeswork.balance.data.database.entity.chat.ChatMessage
import com.beeswork.balance.data.database.entity.swipe.Swipe
import com.beeswork.balance.data.database.entity.login.Login
import com.beeswork.balance.data.database.entity.match.Match
import com.beeswork.balance.data.database.entity.photo.Photo
import com.beeswork.balance.data.database.entity.profile.Profile
import com.beeswork.balance.data.database.entity.setting.FCMToken
import com.beeswork.balance.data.database.entity.setting.Location
import com.beeswork.balance.data.database.entity.setting.PushSetting
import com.beeswork.balance.data.database.entity.card.CardFilter
import com.beeswork.balance.data.database.entity.card.CardPage
import com.beeswork.balance.data.database.entity.match.MatchCount
import com.beeswork.balance.data.database.entity.swipe.SwipeCount


@Database(
    entities = [Match::class, ChatMessage::class, FCMToken::class,
                Swipe::class, Profile::class, Location::class, Photo::class,
                CardFilter::class, PushSetting::class, Login::class, SwipeCount::class, MatchCount::class, CardPage::class],
    version = 1
)
@TypeConverters(
    OffsetDateTimeConverter::class,
    ChatMessageStatusConverter::class,
    UUIDConverter::class,
    ResourceStatusConverter::class,
    PhotoStatusConverter::class,
    UriConverter::class,
    LoginTypeConverter::class,
    LocationPermissionStatusConverter::class,
    LocalDateConverter::class
)
abstract class BalanceDatabase : RoomDatabase() {

    abstract fun matchDAO(): MatchDAO
    abstract fun chatMessageDAO(): ChatMessageDAO
    abstract fun fcmTokenDAO(): FCMTokenDAO
    abstract fun swipeDAO(): SwipeDAO
    abstract fun profileDAO(): ProfileDAO
    abstract fun locationDAO(): LocationDAO
    abstract fun photoDAO(): PhotoDAO
    abstract fun swipeFilterDAO(): CardFilterDAO
    abstract fun settingDAO(): PushSettingDAO
    abstract fun loginDAO(): LoginDAO
    abstract fun swipeCountDAO(): SwipeCountDAO
    abstract fun matchCountDAO(): MatchCountDAO
    abstract fun cardPageDAO(): CardPageDAO

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
