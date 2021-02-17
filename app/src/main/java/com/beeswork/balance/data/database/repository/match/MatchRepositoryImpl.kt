package com.beeswork.balance.data.database.repository.match

import androidx.paging.DataSource
import com.beeswork.balance.data.database.BalanceDatabase
import com.beeswork.balance.data.database.dao.*
import com.beeswork.balance.data.database.entity.ChatMessage
import com.beeswork.balance.data.database.entity.Click
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.database.entity.MatchProfile
import com.beeswork.balance.data.network.rds.chat.ChatRDS
import com.beeswork.balance.data.network.rds.match.MatchRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.internal.mapper.chat.ChatMessageMapper
import com.beeswork.balance.internal.mapper.match.MatchMapper
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.*
import org.threeten.bp.OffsetDateTime
import java.util.*


class MatchRepositoryImpl(
    private val matchRDS: MatchRDS,
    private val chatRDS: ChatRDS,
    private val chatMessageDAO: ChatMessageDAO,
    private val matchDAO: MatchDAO,
    private val matchProfileDAO: MatchProfileDAO,
    private val clickedDAO: ClickedDAO,
    private val clickDAO: ClickDAO,
    private val matchMapper: MatchMapper,
    private val chatMessageMapper: ChatMessageMapper,
    private val balanceDatabase: BalanceDatabase,
    private val preferenceProvider: PreferenceProvider
) : MatchRepository {

    override suspend fun fetchMatches(): Resource<EmptyResponse> {
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
                chatRDS.receivedChatMessages(
                    preferenceProvider.getAccountId(),
                    preferenceProvider.getIdentityToken(),
                    data.receivedChatMessageDTOs.map { it.id }
                )
            }
            saveMatches(data.matchDTOs.map { matchMapper.fromDTOToEntity(it) })
        }
        return Resource.toEmptyResponse(listMatches)
    }

    private fun saveChatMessages(
        sentChatMessages: List<ChatMessage>,
        receivedChatMessages: List<ChatMessage>
    ) {
        // TODO: remove me
        val random = Random()
        for (msg in sentChatMessages) {
            chatMessageDAO.insert(
                ChatMessage(
                    msg.messageId,
                    msg.id,
                    msg.chatId,
                    "message-${random.nextFloat()}",
                    ChatMessageStatus.SENDING,
                    null,
                    OffsetDateTime.now()
                )
            )
        }

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
                } ?: {
                    
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
                    clickDAO.insert(Click(match.matchedId))
                    clickedDAO.deleteById(match.matchedId)
                    matchDAO.insert(match)
                }

            }

            matchProfileDAO.insert(matchProfile)

        }
    }

    private fun saveMatch(match: Match) {
        //add match to the list and call saveMatches()
    }

    override suspend fun getMatches(): DataSource.Factory<Int, Match> {
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