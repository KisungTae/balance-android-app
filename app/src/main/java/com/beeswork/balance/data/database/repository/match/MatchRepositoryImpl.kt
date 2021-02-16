package com.beeswork.balance.data.database.repository.match

import android.icu.util.TimeZone
import androidx.paging.DataSource
import com.beeswork.balance.data.database.BalanceDatabase
import com.beeswork.balance.data.database.dao.ChatMessageDAO
import com.beeswork.balance.data.database.dao.MatchDAO
import com.beeswork.balance.data.database.dao.MatchProfileDAO
import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.database.entity.MatchProfile
import com.beeswork.balance.data.network.rds.match.MatchRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.match.ListMatchesDTO
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.internal.mapper.chat.ChatMessageMapper
import com.beeswork.balance.internal.mapper.match.MatchMapper
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.*
import okhttp3.Dispatcher
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset
import java.lang.Exception
import java.lang.RuntimeException
import java.util.*


class MatchRepositoryImpl(
    private val matchRDS: MatchRDS,
    private val chatMessageDAO: ChatMessageDAO,
    private val matchDAO: MatchDAO,
    private val matchProfileDAO: MatchProfileDAO,
    private val matchMapper: MatchMapper,
    private val chatMessageMapper: ChatMessageMapper,
    private val balanceDatabase: BalanceDatabase,
    private val preferenceProvider: PreferenceProvider
) : MatchRepository {

    override suspend fun fetchMatches(): Resource<EmptyResponse> {


        val time = OffsetDateTime.now(ZoneOffset.UTC)
        val time2 = OffsetDateTime.now()

        println("time: $time | time2: $time2")

        val listMatches = matchRDS.listMatches(
            preferenceProvider.getAccountId(),
            preferenceProvider.getIdentityToken(),
            preferenceProvider.getMatchFetchedAt(),
            preferenceProvider.getAccountFetchedAt(),
            preferenceProvider.getChatMessageFetchedAt()
        )
        if (listMatches.isError()) return Resource.toEmptyResponse(listMatches)
        listMatches.data?.let { data ->
            saveChatMessages(
                data.sentChatMessageDTOs.map { chatMessageMapper.fromDTOToEntity(it) },
                data.receivedChatMessageDTOs.map { chatMessageMapper.fromDTOToEntity(it) }
            )
            CoroutineScope(Dispatchers.IO).launch(CoroutineExceptionHandler { c, t -> }) {
                val list = data.receivedChatMessageDTOs.map { it.id }
                // post the received messages to the server
            }
            saveMatches(data.matchDTOs.map { matchMapper.fromDTOToEntity(it) })
        }
        return Resource.toEmptyResponse(listMatches)
    }

    private fun saveChatMessages(
        sentChatMessages: List<ChatMessage>,
        receivedChatMessages: List<ChatMessage>
    ) {
        balanceDatabase.runInTransaction {
            val matchProfile = matchProfileDAO.findById() ?: MatchProfile()
            matchProfile.chatMessagesInsertedAt = OffsetDateTime.now()

            chatMessageDAO.insertAll(receivedChatMessages)

            for (sentChatMessage in sentChatMessages) {
                chatMessageDAO.updateSentMessage(
                    sentChatMessage.messageId,
                    sentChatMessage.id,
                    sentChatMessage.status,
                    sentChatMessage.createdAt,
                    sentChatMessage.updatedAt,
                )

                sentChatMessage.createdAt?.let {
                    if (it.isAfter(matchProfile.chatMessagesFetchedAt))
                        matchProfile.chatMessagesFetchedAt = it
                }
            }
            matchProfileDAO.insert(matchProfile)
        }
    }

    private fun saveMatches(matches: List<Match>) {
        balanceDatabase.runInTransaction {
            val matchProfile = matchProfileDAO.findById() ?: MatchProfile()
            for (match in matches) {
                if (match.updatedAt.isAfter(matchProfile.matchFetchedAt))
                    matchProfile.matchFetchedAt = match.updatedAt

                if (match.accountUpdatedAt.isAfter(matchProfile.accountFetchedAt))
                    matchProfile.accountFetchedAt = match.accountUpdatedAt

                val lastReadChatMessageId = matchDAO.findLastReadChatMessageId(match.chatId)

                match.unreadMessageCount = chatMessageDAO.countAllAfter(
                    match.chatId,
                    lastReadChatMessageId
                )

                chatMessageDAO.findLastProcessed(match.chatId)?.let {
                    match.updatedAt = it.createdAt
                    match.recentMessage = it.body
                }

                if (matchDAO.existsByChatId(match.chatId))
                    matchDAO.updateMatch(
                        match.chatId,
                        match.unmatched,
                        match.updatedAt,
                        match.name,
                        match.repPhotoKey,
                        match.blocked,
                        match.deleted,
                        match.accountUpdatedAt,
                        match.unreadMessageCount,
                        match.recentMessage
                    )
                else {
                    chatMessageDAO.insert(
                        ChatMessage.getTailChatMessage(
                            match.chatId,
                            match.updatedAt
                        )
                    )
                    chatMessageDAO.insert(
                        ChatMessage.getHeadChatMessage(
                            match.chatId,
                            match.updatedAt
                        )
                    )
                    matchDAO.insert(match)
                }
            }
        }
    }


    private fun saveMatchess(data: ListMatchesDTO) {
        val chatMessagesInsertedAt = OffsetDateTime.now()
        val matches = data.matchDTOs.map { matchMapper.fromDTOToEntity(it) }
        val sentChatMessages = data.sentChatMessageDTOs.map {
            chatMessageMapper.fromDTOToEntity(it)
        }
        val receivedChatMessages = data.receivedChatMessageDTOs.map {
            chatMessageMapper.fromDTOToEntity(it)
        }

        // TODO: remove me
        val random = Random()
        for (i in sentChatMessages.indices) {
            val msg = sentChatMessages[i]
            val body = "message-${random.nextFloat()}"
            chatMessageDAO.insert(
                ChatMessage(
                    msg.messageId,
                    null,
                    msg.chatId,
                    body,
                    ChatMessageStatus.SENDING,
                    null,
                    OffsetDateTime.now()
                )
            )
        }


        balanceDatabase.runInTransaction {
            val matchProfile = matchProfileDAO.findById() ?: MatchProfile()
            matchProfile.chatMessagesInsertedAt = chatMessagesInsertedAt
            matchProfileDAO.insert(matchProfile)

            chatMessageDAO.insertAll(receivedChatMessages)

            for (i in sentChatMessages.indices) {
                val chatMessage = sentChatMessages[i]

                chatMessage.createdAt?.let {
                    if (it.isAfter(matchProfile.chatMessagesFetchedAt))
                        matchProfile.chatMessagesFetchedAt = it
                }

                chatMessageDAO.updateSentMessage(
                    chatMessage.messageId,
                    chatMessage.id,
                    chatMessage.status,
                    chatMessage.createdAt,
                    chatMessage.updatedAt,
                )
            }

            for (i in matches.indices) {
                val match = matches[i]

                if (match.updatedAt.isAfter(matchProfile.matchFetchedAt))
                    matchProfile.matchFetchedAt = match.updatedAt

                if (match.accountUpdatedAt.isAfter(matchProfile.accountFetchedAt))
                    matchProfile.accountFetchedAt = match.accountUpdatedAt

                val lastReadChatMessageId = matchDAO.findLastReadChatMessageId(match.chatId)
                match.unreadMessageCount = chatMessageDAO.countAllAfter(
                    match.chatId,
                    lastReadChatMessageId
                )
                chatMessageDAO.findLastProcessed(match.chatId)?.let {
                    match.updatedAt = it.createdAt
                    match.recentMessage = it.body
                }

                if (matchDAO.existsByChatId(match.chatId))
                    matchDAO.updateMatch(
                        match.chatId,
                        match.unmatched,
                        match.updatedAt,
                        match.name,
                        match.repPhotoKey,
                        match.blocked,
                        match.deleted,
                        match.accountUpdatedAt,
                        match.unreadMessageCount,
                        match.recentMessage
                    )
                else {
                    chatMessageDAO.insert(
                        ChatMessage.getTailChatMessage(
                            match.chatId,
                            match.updatedAt
                        )
                    )
                    chatMessageDAO.insert(
                        ChatMessage.getHeadChatMessage(
                            match.chatId,
                            match.updatedAt
                        )
                    )
                    matchDAO.insert(match)
                }
            }
            matchProfileDAO.insert(matchProfile)
        }
    }


    override suspend fun getMatches(): DataSource.Factory<Int, Match> {
        // TODO: reset the unread count
        return matchDAO.getMatches()
    }

    override suspend fun change() {

//        val matches = matchDAO.findAll()
//        for (i in matches.indices) {
//            val match = matches[i]
//            println("match.lastReadChatMessageId = 999")
//            match.lastReadChatMessageId = 123456
//        }
//        println("matchDAO.insertAll(matches)")
//        matchDAO.insertAll(matches)

//        balanceDatabase.runInTransaction {
//            val match = matchDAO.existsByChatId(1)
//            println("matchDAO.existsByChatId(1): $match")
//            val matches = matchDAO.findAll()
//            for (i in matches.indices) {
//                val match = matches[i]
//                println("match.lastReadChatMessageId = 999")
//                match.lastReadChatMessageId = 777
//            }
//            matchDAO.insertAll(matches)
//        }
    }
}


// TODO: need to send receivedChatMessages to make them read = true on server
// TODO: decide chatprofile or matchprofile
// TODO: transaction save chatfetchedat and chatmessageinserted at then save chatmessages with updatedAt
// TODO: findallunprocsssed order by case when ChatMessageStatus = HEAD then 0


// Query
// select * from chatMessage where chatId = 12 and id is not null and id > 0
// select * from `match`
// select * from chatMessage where chatId = 12 and id is not null order by id desc limit 1
// message-0.88725746
// delete from chatMessage
// delete from chatMessage where status not in (5,6)