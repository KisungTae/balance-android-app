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
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.internal.mapper.chat.ChatMessageMapper
import com.beeswork.balance.internal.mapper.match.MatchMapper
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.*
import org.threeten.bp.OffsetDateTime
import kotlin.random.Random


class MatchRepositoryImpl(
    private val matchRDS: MatchRDS,
    private val chatRDS: ChatRDS,
    private val chatMessageDAO: ChatMessageDAO,
    private val matchDAO: MatchDAO,
    private val fetchMatchesResultDAO: FetchMatchesResultDAO,
    private val clickerDAO: ClickerDAO,
    private val clickedDAO: ClickedDAO,
    private val matchMapper: MatchMapper,
    private val chatMessageMapper: ChatMessageMapper,
    private val balanceDatabase: BalanceDatabase,
    private val preferenceProvider: PreferenceProvider
) : MatchRepository {

    override suspend fun listMatches(): DataSource.Factory<Int, Match> {
        return withContext(Dispatchers.IO) {
            return@withContext matchDAO.findAllPaged()
        }
    }

    override suspend fun fetchMatches(): Resource<EmptyResponse> {
        updateFetchMatchesResult(Resource.Status.LOADING)
        val listMatches = matchRDS.listMatches(
            preferenceProvider.getAccountId(),
            preferenceProvider.getIdentityToken(),
            preferenceProvider.getMatchFetchedAt()
        )

        if (listMatches.isError()) {
            updateFetchMatchesResult(listMatches.status)
            return Resource.toEmptyResponse(listMatches)
        }

        listMatches.data?.let { data ->
            saveChatMessages(
                OffsetDateTime.now(),
                data.sentChatMessageDTOs.map { chatMessageMapper.fromDTOToEntity(it) },
                data.receivedChatMessageDTOs.map { chatMessageMapper.fromDTOToEntity(it) }
            )
            syncChatMessages(data.sentChatMessageDTOs, data.receivedChatMessageDTOs)
            saveMatches(data.matchDTOs.map { matchMapper.fromDTOToEntity(it) })
            updateFetchMatchesResult(listMatches.status)
            preferenceProvider.putMatchFetchedAt(data.fetchedAt)

            //      TODO: remove me
            saveSentChatMessages(data.sentChatMessageDTOs.map { chatMessageMapper.fromDTOToEntity(it) })

        }
        return Resource.toEmptyResponse(listMatches)
    }

    private fun updateFetchMatchesResult(status: Resource.Status) {
        val fetchMatchesResult = fetchMatchesResultDAO.findById() ?: FetchMatchesResult()
        fetchMatchesResult.status = status
        fetchMatchesResultDAO.insert(fetchMatchesResult)
    }

    private fun saveChatMessages(
        chatMessagesInsertedAt: OffsetDateTime,
        sentChatMessages: List<ChatMessage>,
        receivedChatMessages: List<ChatMessage>
    ) {
        balanceDatabase.runInTransaction {
            val fetchMatchesResult = fetchMatchesResultDAO.findById() ?: FetchMatchesResult()
            fetchMatchesResult.chatMessagesInsertedAt = chatMessagesInsertedAt

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
            chatRDS.syncChatMessages(
                preferenceProvider.getAccountId(),
                preferenceProvider.getIdentityToken(),
                sentChatMessages.map { it.id },
                receivedChatMessages.map { it.id }
            )
        }
    }

    private fun saveMatches(matches: List<Match>) {
        balanceDatabase.runInTransaction {
            for (newMatch in matches) {
                updateMatch(newMatch)
                clickerDAO.deleteById(newMatch.matchedId)
                clickedDAO.insert(Clicked(newMatch.matchedId))
                matchDAO.insert(newMatch)
            }
        }
    }

    private fun updateMatch(newMatch: Match) {
        matchDAO.findById(newMatch.chatId)?.let { match ->
            match.unmatched = newMatch.unmatched
            match.deleted = newMatch.deleted
            match.active = newMatch.active
            match.repPhotoKey = newMatch.repPhotoKey
        } ?: kotlin.run {
            chatMessageDAO.insert(ChatMessage.getTail(newMatch.chatId, newMatch.updatedAt))
            chatMessageDAO.insert(ChatMessage.getHead(newMatch.chatId, newMatch.updatedAt))
        }
        updateRecentChatMessage(newMatch)
    }

    private fun updateRecentChatMessage(match: Match) {
        if (!match.isValid()) return
        chatMessageDAO.findMostRecentAfter(
            match.chatId,
            match.lastReadChatMessageId
        )?.let { chatMessage ->
            match.recentChatMessage = chatMessage.body
            match.updatedAt = chatMessage.createdAt
            match.active = true
            match.unread = true
        }
    }

    //  TODO: remove me
    private fun saveSentChatMessages(sentChatMessages: List<ChatMessage>) {
        val matches = matchDAO.findAll()
        val chatIds = matches.map { it.chatId }
        for (msg in sentChatMessages) {
            val randomIndex = Random.nextInt(0, chatIds.size - 1)
            chatMessageDAO.insert(
                ChatMessage(
                    msg.messageId,
                    msg.id,
                    chatIds.get(randomIndex),
                    "message-${Random.nextFloat()}",
                    ChatMessageStatus.SENDING,
                    OffsetDateTime.now(),
                    OffsetDateTime.now()
                )
            )
        }
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