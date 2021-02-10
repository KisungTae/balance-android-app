package com.beeswork.balance.data.database.repository.match

import androidx.room.RoomDatabase
import com.beeswork.balance.data.database.dao.ChatMessageDAO
import com.beeswork.balance.data.database.dao.MatchDAO
import com.beeswork.balance.data.network.rds.match.MatchRDS
import com.beeswork.balance.internal.provider.preference.PreferenceProvider

class MatchRepositoryImpl(
    private val matchRDS: MatchRDS,
    private val chatMessageDAO: ChatMessageDAO,
    private val matchDAO: MatchDAO,
    private val preferenceProvider: PreferenceProvider
) : MatchRepository {
    override suspend fun fetchMatches() {
        val listMatchResponse = matchRDS.listMatches(
            preferenceProvider.getAccountId(),
            preferenceProvider.getIdentityToken(),
            preferenceProvider.getMatchFetchedAt(),
            preferenceProvider.getAccountFetchedAt(),
            preferenceProvider.getChatMessageFetchedAt()
        )






        //TODO: error

        // TODO: need to send receivedChatMessages to make them read = true on server
        // TODO: decide chatprofile or matchprofile
        // TODO: transaction save chatfetchedat and chatmessageinserted at then save chatmessages with updatedAt

        println(listMatchResponse.errorMessage)

    }
}