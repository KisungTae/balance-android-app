package com.beeswork.balance.data.database.repository.match

import androidx.paging.DataSource
import com.beeswork.balance.data.database.BalanceDatabase
import com.beeswork.balance.data.database.dao.ChatMessageDAO
import com.beeswork.balance.data.database.dao.MatchDAO
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.network.rds.match.MatchRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.mapper.match.MatchMapper
import com.beeswork.balance.internal.provider.preference.PreferenceProvider

class MatchRepositoryImpl(
    private val matchRDS: MatchRDS,
    private val chatMessageDAO: ChatMessageDAO,
    private val matchDAO: MatchDAO,
    private val preferenceProvider: PreferenceProvider,
    private val matchMapper: MatchMapper,
    private val balanceDatabase: BalanceDatabase
) : MatchRepository {
    override suspend fun fetchMatches(): Resource<EmptyResponse> {
        val listMatches = matchRDS.listMatches(
            preferenceProvider.getAccountId(),
            preferenceProvider.getIdentityToken(),
            preferenceProvider.getMatchFetchedAt(),
            preferenceProvider.getAccountFetchedAt(),
            preferenceProvider.getChatMessageFetchedAt()
        )

        if (listMatches.isError())
            return Resource.toEmptyResponse(listMatches)

        val matches = listMatches.data?.matchDTOs?.map { matchMapper.fromDTOToEntity(it) }

        println("match size: ${matches?.size}")
        listMatches.data?.let {
            val abc = it.matchDTOs.map { matchResponse ->
                Match(
                    matchResponse.chatId,
                    matchResponse.matchedId,
                    matchResponse.unmatched,
                    matchResponse.updatedAt,
                    matchResponse.name,
                    matchResponse.repPhotoKey,
                    matchResponse.blocked,
                    matchResponse.deleted,
                    matchResponse.accountUpdatedAt
                )
            }


            println("abc size: ${abc.size}")
        }





        balanceDatabase.runInTransaction({

        })







        //TODO: error

        // TODO: need to send receivedChatMessages to make them read = true on server
        // TODO: decide chatprofile or matchprofile
        // TODO: transaction save chatfetchedat and chatmessageinserted at then save chatmessages with updatedAt

        println(listMatches.errorMessage)

        return Resource.toEmptyResponse(listMatches)

    }

    override suspend fun getMatches(): DataSource.Factory<Int, Match> {
        // TODO: reset the unread count
        return matchDAO.getMatches()
    }
}