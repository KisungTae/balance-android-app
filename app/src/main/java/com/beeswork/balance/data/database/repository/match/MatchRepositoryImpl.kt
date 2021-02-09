package com.beeswork.balance.data.database.repository.match

import com.beeswork.balance.data.database.dao.ChatMessageDAO
import com.beeswork.balance.data.database.dao.MatchDAO
import com.beeswork.balance.data.network.rds.match.MatchRDS
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter

class MatchRepositoryImpl(
    private val matchRDS: MatchRDS,
    private val chatMessageDAO: ChatMessageDAO,
    private val matchDAO: MatchDAO,
    private val preferenceProvider: PreferenceProvider
) : MatchRepository {
    override suspend fun fetchMatches() {
        val listMatchResponse = matchRDS.listMatches(
            preferenceProvider.getAccountUUID(),
            preferenceProvider.getIdentityTokenUUID(),
            preferenceProvider.getLastFetchedMatchUpdatedAt(),
            preferenceProvider.getLastFetchedAccountUpdatedAt(),
            preferenceProvider.getLastFetchedChatMessageCreatedAt()
        )


        println(listMatchResponse.errorMessage)

    }
}