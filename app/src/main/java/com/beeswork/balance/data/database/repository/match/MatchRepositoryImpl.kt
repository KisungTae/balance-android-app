package com.beeswork.balance.data.database.repository.match

import androidx.paging.DataSource
import com.beeswork.balance.data.database.BalanceDatabase
import com.beeswork.balance.data.database.dao.ChatMessageDAO
import com.beeswork.balance.data.database.dao.MatchDAO
import com.beeswork.balance.data.database.dao.MatchProfileDAO
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.network.rds.match.MatchRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.ChatMessageStatus
import com.beeswork.balance.internal.mapper.chat.ChatMessageMapper
import com.beeswork.balance.internal.mapper.match.MatchMapper
import com.beeswork.balance.internal.provider.preference.PreferenceProvider


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


        val lastReadChatMessageId = matchDAO.findLastReadChatMessageId(100)
        val lastChatMessage = chatMessageDAO.findLastProcessed(3)

        println("sav: $lastReadChatMessageId")



//        val listMatches = matchRDS.listMatches(
//            preferenceProvider.getAccountId(),
//            preferenceProvider.getIdentityToken(),
//            preferenceProvider.getMatchFetchedAt(),
//            preferenceProvider.getAccountFetchedAt(),
//            preferenceProvider.getChatMessageFetchedAt()
//        )
//
//        if (listMatches.isError())
//            return Resource.toEmptyResponse(listMatches)
//
//        listMatches.data?.let { data ->
//
//            val chatMessagesInsertedAt = OffsetDateTime.now()
//            val matches = data.matchDTOs.map { matchMapper.fromDTOToEntity(it) }
//            val sentChatMessages = data.sentChatMessageDTOs.map {
//                chatMessageMapper.fromDTOToEntity(it)
//            }
//            val receivedChatMessages = data.receivedChatMessageDTOs.map {
//                chatMessageMapper.fromDTOToEntity(it)
//            }
//
//            balanceDatabase.runInTransaction {
//
//                val matchProfile = matchProfileDAO.findById() ?: MatchProfile()
//                matchProfile.chatMessagesInsertedAt = chatMessagesInsertedAt
//                matchProfileDAO.insert(matchProfile)
//
//
//
//                for (i in sentChatMessages.indices) {
//                    val chatMessage = sentChatMessages[i]
//
//                    chatMessage.createdAt?.let {
//                        if (it.isAfter(matchProfile.chatMessagesFetchedAt))
//                            matchProfile.chatMessagesFetchedAt = it
//                    }
//
//                    chatMessageDAO.updateSentMessage(
//                        chatMessage.messageId,
//                        chatMessage.id,
//                        chatMessage.createdAt,
//                        chatMessage.updatedAt
//                    )
//
//                }
//
//                chatMessageDAO.insertAll(receivedChatMessages)
//
//                for (i in matches.indices) {
//                    val match = matches[i]
//
//                    if (match.updatedAt.isAfter(matchProfile.matchFetchedAt))
//                        matchProfile.matchFetchedAt = match.updatedAt
//
//                    if (match.accountUpdatedAt.isAfter(matchProfile.accountFetchedAt))
//                        matchProfile.accountFetchedAt = match.accountUpdatedAt
//
//                    val lastReadChatMessageId = matchDAO.findLastReadChatMessageId(match.chatId) ?: 0
//                    val unreadMessageCount = chatMessageDAO.countAllAfter(match.chatId, lastReadChatMessageId)
//                    val lastChatMessage = chatMessageDAO.findLastByChatId(match.chatId)
//
//                    matchDAO.findLastReadChatMessageId(match.chatId)?.let {
//
//
//
//                    } ?: {
//
//                    }
//
//
//                    if (matchDAO.existsByChatId(match.chatId))
//
//
//                        matchDAO.updateMatch(
//                            match.chatId,
//                            match.unmatched,
//                            match.updatedAt,
//                            match.name,
//                            match.repPhotoKey,
//                            match.blocked,
//                            match.deleted,
//                            match.accountUpdatedAt
//                        )
//                    else {
//                        chatMessageDAO.insert(
//                            ChatMessage.getTailChatMessage(
//                                match.chatId,
//                                match.updatedAt
//                            )
//                        )
//                        chatMessageDAO.insert(
//                            ChatMessage.getHeadChatMessage(
//                                match.chatId,
//                                match.updatedAt
//                            )
//                        )
//                        matchDAO.insert(match)
//                    }
//
//
//                }
//
//                matchProfileDAO.insert(matchProfile)
//
//            }
//
//
//        }

        // TODO: need to send receivedChatMessages to make them read = true on server
        // TODO: decide chatprofile or matchprofile
        // TODO: transaction save chatfetchedat and chatmessageinserted at then save chatmessages with updatedAt
        // TODO: findallunprocsssed order by case when ChatMessageStatus = HEAD then 0

//        return Resource.toEmptyResponse(listMatches)


        balanceDatabase.runInTransaction {
            val matches = matchDAO.findAll()
            for (i in matches.indices) {
                val match = matches[i]
                println("match.lastReadChatMessageId = 999984")

                for (j in 1..10000000) {
                    match.lastReadChatMessageId = 999984
                }
            }
            matchDAO.insertAll(matches)
        }

        return Resource.success(EmptyResponse())
    }


    private fun saveMatches() {

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