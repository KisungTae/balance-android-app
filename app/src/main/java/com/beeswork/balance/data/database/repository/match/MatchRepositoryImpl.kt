package com.beeswork.balance.data.database.repository.match

import androidx.paging.DataSource
import com.beeswork.balance.data.database.BalanceDatabase
import com.beeswork.balance.data.database.dao.*
import com.beeswork.balance.data.database.entity.*
import com.beeswork.balance.data.network.rds.chat.ChatRDS
import com.beeswork.balance.data.network.rds.match.MatchRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.chat.ChatMessageDTO
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.mapper.chat.ChatMessageMapper
import com.beeswork.balance.internal.mapper.match.MatchMapper
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.*
import org.threeten.bp.OffsetDateTime


class MatchRepositoryImpl(
    private val matchRDS: MatchRDS,
    private val chatRDS: ChatRDS,
    private val chatMessageDAO: ChatMessageDAO,
    private val matchDAO: MatchDAO,
    private val fetchMatchesResultDAO: FetchMatchesResultDAO,
    private val clickedDAO: ClickedDAO,
    private val clickDAO: ClickDAO,
    private val matchMapper: MatchMapper,
    private val chatMessageMapper: ChatMessageMapper,
    private val balanceDatabase: BalanceDatabase,
    private val preferenceProvider: PreferenceProvider
) : MatchRepository {

    override suspend fun fetchMatches(): Resource<EmptyResponse> {
        updateFetchMatchesResultStatus(Resource.Status.LOADING)
        val listMatches = matchRDS.listMatches(
            preferenceProvider.getAccountId(),
            preferenceProvider.getIdentityToken(),
            preferenceProvider.getMatchFetchedAt()
        )

        if (listMatches.isError()) {
            updateFetchMatchesResultStatus(listMatches.status)
            return Resource.toEmptyResponse(listMatches)
        }

        listMatches.data?.let { data ->
            saveChatMessages(
                data.sentChatMessageDTOs.map { chatMessageMapper.fromDTOToEntity(it) },
                data.receivedChatMessageDTOs.map { chatMessageMapper.fromDTOToEntity(it) }
            )
            syncChatMessages(data.sentChatMessageDTOs, data.receivedChatMessageDTOs)
            saveMatches(data.matchDTOs.map { matchMapper.fromDTOToEntity(it) })
            updateFetchMatchesResultStatus(listMatches.status)
        }
        return Resource.toEmptyResponse(listMatches)
    }

    private fun updateFetchMatchesResultStatus(status: Resource.Status) {
        val fetchMatchesResult = fetchMatchesResultDAO.findById() ?: FetchMatchesResult()
        fetchMatchesResult.status = status
        fetchMatchesResultDAO.insert(fetchMatchesResult)
    }

    private fun saveChatMessages(
        sentChatMessages: List<ChatMessage>,
        receivedChatMessages: List<ChatMessage>
    ) {
        balanceDatabase.runInTransaction {
            val fetchMatchesResult = fetchMatchesResultDAO.findById() ?: FetchMatchesResult()
            fetchMatchesResult.chatMessagesInsertedAt = OffsetDateTime.now()

            chatMessageDAO.insertAll(receivedChatMessages)
            for (sentChatMessage in sentChatMessages) {
                chatMessageDAO.updateSentMessage(
                    sentChatMessage.messageId,
                    sentChatMessage.id,
                    sentChatMessage.status,
                    sentChatMessage.createdAt,
                    sentChatMessage.updatedAt,
                )
            }
            fetchMatchesResultDAO.insert(fetchMatchesResult)
        }
    }

    private fun syncChatMessages(
        sentChatMessages: List<ChatMessageDTO>,
        receivedChatMessages: List<ChatMessageDTO>
    ) {
        if (sentChatMessages.isEmpty() && receivedChatMessages.isEmpty()) return

        CoroutineScope(Dispatchers.IO).launch(CoroutineExceptionHandler { c, t -> }) {
            val chatMessageIds = mutableListOf<Long>()
            chatMessageIds.addAll(sentChatMessages.map { it.id })
            chatMessageIds.addAll(receivedChatMessages.map { it.id })

            chatRDS.syncChatMessages(
                preferenceProvider.getAccountId(),
                preferenceProvider.getIdentityToken(),
                chatMessageIds
            )
        }
    }


    //  TODO: remove me
    private fun testSaveSentChatMessages() {
//        val random = Random()
//        for (msg in sentChatMessages) {
//            chatMessageDAO.insert(
//                ChatMessage(
//                    msg.messageId,
//                    msg.id,
//                    msg.chatId,
//                    "message-${random.nextFloat()}",
//                    ChatMessageStatus.SENDING,
//                    null,
//                    OffsetDateTime.now()
//                )
//            )
//        }
    }

    private fun saveMatches(matches: List<Match>) {
        balanceDatabase.runInTransaction {
            for (match in matches) {
                val lastReadChatMessageId = matchDAO.findLastReadChatMessageId(match.chatId)
                lastReadChatMessageId?.let {

                } ?: kotlin.run {
                    chatMessageDAO.insert(ChatMessage.getTail(match.chatId, match.updatedAt))
                    chatMessageDAO.insert(ChatMessage.getHead(match.chatId, match.updatedAt))
                    matchDAO.insert(match)

                }

                match.unreadMessageCount = chatMessageDAO.countAllAfter(
                    match.chatId,
                    lastReadChatMessageId
                )

                chatMessageDAO.findLastProcessed(match.chatId)?.let {
                    match.updatedAt = it.createdAt
                    match.recentMessage = it.body
                } ?: {
                    match.recentMessage = null
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
                        ChatMessage.getTail(
                            match.chatId,
                            match.updatedAt
                        )
                    )
                    chatMessageDAO.insert(
                        ChatMessage.getHead(
                            match.chatId,
                            match.updatedAt
                        )
                    )
                    clickDAO.insert(Click(match.matchedId))
                    clickedDAO.deleteById(match.matchedId)
                }

            }
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