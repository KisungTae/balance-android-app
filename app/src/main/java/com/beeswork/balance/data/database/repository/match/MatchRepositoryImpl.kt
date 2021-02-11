package com.beeswork.balance.data.database.repository.match

import androidx.paging.DataSource
import com.beeswork.balance.data.database.dao.ChatMessageDAO
import com.beeswork.balance.data.database.dao.MatchDAO
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.network.rds.match.MatchRDS
import com.beeswork.balance.internal.mapper.match.MatchMapper
import com.beeswork.balance.internal.provider.preference.PreferenceProvider

class MatchRepositoryImpl(
    private val matchRDS: MatchRDS,
    private val chatMessageDAO: ChatMessageDAO,
    private val matchDAO: MatchDAO,
    private val preferenceProvider: PreferenceProvider,
    private val matchMapper: MatchMapper
) : MatchRepository {
    override suspend fun fetchMatches() {
        val listMatchDTO = matchRDS.listMatches(
            preferenceProvider.getAccountId(),
            preferenceProvider.getIdentityToken(),
            preferenceProvider.getMatchFetchedAt(),
            preferenceProvider.getAccountFetchedAt(),
            preferenceProvider.getChatMessageFetchedAt()
        )

        val matches = listMatchDTO.data?.matchDTOs?.map { matchMapper.fromDTOToEntity(it) }

        println("match size: ${matches?.size}")
        listMatchDTO.data?.let {
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




        //TODO: error

        // TODO: need to send receivedChatMessages to make them read = true on server
        // TODO: decide chatprofile or matchprofile
        // TODO: transaction save chatfetchedat and chatmessageinserted at then save chatmessages with updatedAt

        println(listMatchDTO.errorMessage)

    }

    override suspend fun getMatches(): DataSource.Factory<Int, Match> {
        return matchDAO.getMatches()
    }
}