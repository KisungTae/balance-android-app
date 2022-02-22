package com.beeswork.balance.data.network.service.fcm

import com.beeswork.balance.data.database.repository.chat.ChatRepository
import com.beeswork.balance.data.database.repository.swipe.SwipeRepository
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.data.network.response.match.MatchDTO
import com.beeswork.balance.data.network.response.swipe.SwipeDTO
import com.beeswork.balance.internal.constant.PushType
import com.beeswork.balance.internal.constant.StompHeader
import com.beeswork.balance.internal.provider.gson.GsonProvider
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance


class FCMServiceImpl : FirebaseMessagingService(), KodeinAware {

    override val kodein by closestKodein()

    private val settingRepository: SettingRepository by instance()
    private val matchRepository: MatchRepository by instance()
    private val swipeRepository: SwipeRepository by instance()
    private val chatRepository: ChatRepository by instance()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        scope.launch {
            settingRepository.saveFCMToken(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val pushType = remoteMessage.data[StompHeader.PUSH_TYPE]?.let {
            PushType.valueOf(it)
        }
        when (pushType) {
            PushType.SWIPE -> scope.launch {
                val json = GsonProvider.gson.toJsonTree(remoteMessage.data)
                swipeRepository.saveSwipe(GsonProvider.gson.fromJson(json, SwipeDTO::class.java))
            }
            PushType.MATCH -> scope.launch {
                val json = GsonProvider.gson.toJsonTree(remoteMessage.data)
                val match = GsonProvider.gson.fromJson(json, MatchDTO::class.java)
                println(match)
//                matchRepository.saveMatch(GsonProvider.gson.fromJson(json, MatchDTO::class.java))
            }
            PushType.CHAT_MESSAGE -> scope.launch {
                val json = GsonProvider.gson.toJsonTree(remoteMessage.data)
                val chatMessage = GsonProvider.gson.fromJson(json, ChatMessageDTO::class.java)
                println(chatMessage)
//                chatRepository.saveChatMessageReceived(GsonProvider.gson.fromJson(json, ChatMessageDTO::class.java))
            }
            else -> {}
        }
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }
}