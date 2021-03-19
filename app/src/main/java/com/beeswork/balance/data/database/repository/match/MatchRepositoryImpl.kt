package com.beeswork.balance.data.database.repository.match

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
import org.threeten.bp.ZoneOffset
import java.util.*
import kotlin.random.Random


class MatchRepositoryImpl(
    private val matchRDS: MatchRDS,
    private val chatRDS: ChatRDS,
    private val chatMessageDAO: ChatMessageDAO,
    private val matchDAO: MatchDAO,
    private val fetchMatchesResultDAO: FetchMatchesResultDAO,
    private val clickerDAO: ClickerDAO,
    private val clickedDAO: ClickedDAO,
    private val matchProfileDAO: MatchProfileDAO,
    private val matchMapper: MatchMapper,
    private val chatMessageMapper: ChatMessageMapper,
    private val balanceDatabase: BalanceDatabase,
    private val preferenceProvider: PreferenceProvider
) : MatchRepository {

    override suspend fun prependMatches(pageSize: Int, chatId: Long): List<Match> {

        return listOf()
    }

    override suspend fun appendMatches(pageSize: Int, chatId: Long): List<Match> {

        return listOf()
    }


    override suspend fun fetchMatches(): Resource<EmptyResponse> {
        updateMatchProfileStatus(Resource.Status.LOADING)
        val listMatches = matchRDS.listMatches(
            preferenceProvider.getAccountId(),
            preferenceProvider.getIdentityToken(),
            preferenceProvider.getMatchFetchedAt()
        )

        if (listMatches.isError()) {
            updateMatchProfileStatus(listMatches.status)
            return Resource.toEmptyResponse(listMatches)
        }

        listMatches.data?.let { data ->
            //      TODO: remove me
            saveSentChatMessages(
                data.sentChatMessageDTOs.map { chatMessageMapper.fromDTOToEntity(it) },
                data.matchDTOs.map { matchMapper.fromDTOToEntity(it) }
            )

            saveChatMessages(
                data.sentChatMessageDTOs.map { chatMessageMapper.fromDTOToEntity(it) },
                data.receivedChatMessageDTOs.map { chatMessageMapper.fromDTOToEntity(it) }
            )
            syncChatMessages(data.sentChatMessageDTOs, data.receivedChatMessageDTOs)
            saveMatches(data.matchDTOs.map { matchMapper.fromDTOToEntity(it) }.toMutableList())
            updateMatchProfileStatus(listMatches.status)
            preferenceProvider.putMatchFetchedAt(data.fetchedAt)
        }
        return Resource.toEmptyResponse(listMatches)
    }


    private fun updateMatchProfileStatus(status: Resource.Status) {
        val matchProfile = getMatchProfile()
        matchProfile.fetchMatchesStatus = status
        matchProfileDAO.insert(matchProfile)
    }

    private fun getMatchProfile(): MatchProfile {
        return matchProfileDAO.findById() ?: MatchProfile()
    }

    private fun saveChatMessages(
        sentChatMessages: List<ChatMessage>,
        receivedChatMessages: List<ChatMessage>
    ) {
        balanceDatabase.runInTransaction {
            chatMessageDAO.insert(receivedChatMessages)
            for (sentChatMessage in sentChatMessages) {
                chatMessageDAO.updateSentMessage(
                    sentChatMessage.messageId,
                    sentChatMessage.id,
                    sentChatMessage.status,
                    sentChatMessage.createdAt
                )
            }
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

    private fun saveMatches(matches: MutableList<Match>) {
        balanceDatabase.runInTransaction {
            for (match in matches) {
                updateMatch(match)
            }
            matches.sortWith(compareBy({ it.updatedAt }, { it.chatId }))

            val matchProfile = getMatchProfile()
            for (match in matches) {
                updateRowId(match, matchProfile)
                clickerDAO.deleteById(match.matchedId)
                clickedDAO.insert(Clicked(match.matchedId))
                matchDAO.insert(match)
            }
            matchProfileDAO.insert(matchProfile)
        }
    }

    private fun updateRowId(match: Match, matchProfile: MatchProfile) {
        if (match.isValid()) {
            matchProfile.lastMatchRowId = (matchProfile.lastMatchRowId + 1)
            match.rowId = matchProfile.lastMatchRowId
        } else {
            matchProfile.lastUnmatchRowId = (matchProfile.lastUnmatchRowId + 1)
            match.rowId = matchProfile.lastUnmatchRowId
        }
    }

    private fun updateMatch(match: Match) {
        matchDAO.findById(match.chatId)?.let {
            match.updatedAt = it.updatedAt
            match.unread = it.unread
            match.recentChatMessage = it.recentChatMessage
            match.lastReadChatMessageId = it.lastReadChatMessageId
        }
        updateRecentChatMessage(match)
    }

    private fun updateRecentChatMessage(match: Match) {
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
    private fun saveSentChatMessages(sentChatMessages: List<ChatMessage>, matches: List<Match>) {
        val chatIds = matches.map { it.chatId }
        for (msg in sentChatMessages) {
            val randomIndex = Random.nextInt(0, chatIds.size - 1)
            chatMessageDAO.insert(
                ChatMessage(
                    msg.messageId,
                    msg.id,
                    chatIds[randomIndex],
                    "message-${Random.nextFloat()}",
                    ChatMessageStatus.SENDING,
                    OffsetDateTime.now(ZoneOffset.UTC)
                )
            )
        }
    }

    //  TODO: remove me
    override fun testFunction() {

        val dummyMatches = mutableListOf<Match>()


        for (i in 0..50) {
            dummyMatches.add(
                Match(
                    239020392L + Random.nextInt(1000),
                    UUID.randomUUID(),
                    false,
                    false,
                    "user-test new inserted",
                    "",
                    false,
                    OffsetDateTime.now()
                )
            )
        }

        CoroutineScope(Dispatchers.IO).launch {
            matchDAO.insert(dummyMatches)
//            matchDAO.insert(dummyMatches[0])


//            matchDAO.findById(3844)?.let {
//                it.updatedAt = OffsetDateTime.now()
//                it.name = "this is updated user"
//                matchDAO.insert(it)
//            }
        }

    }
}


// TODO: when append and prepend, check if rowId is the same on chatId if different then refresh whole list so pass the first match's chatId and wholePageSize
