package com.beeswork.balance.data.database.repository.match

import androidx.paging.DataSource
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import org.threeten.bp.OffsetDateTime

interface MatchRepository {
    suspend fun loadMatches(loadSize: Int, startPosition: Int, searchKeyword: String): List<Match>?
    suspend fun loadMatches(loadSize: Int, startPosition: Int): List<Match>?
    suspend fun fetchMatches(): Resource<EmptyResponse>
    suspend fun prependMatches(pageSize: Int, headUpdatedAt: OffsetDateTime): List<Match>
    suspend fun appendMatches(pageSize: Int, tailUpdatedAt: OffsetDateTime): List<Match>

//  TODO: remove me
    suspend fun loadMatchesAsFactory(): DataSource.Factory<Int, Match>
    fun testFunction()
}