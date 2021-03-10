package com.beeswork.balance.data.database.repository.match

import androidx.paging.DataSource
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse

interface MatchRepository {
    suspend fun loadMatches(loadSize: Int, startPosition: Int, keyword: String): List<Match>?
    suspend fun loadMatches(loadSize: Int, startPosition: Int): List<Match>?
    suspend fun fetchMatches(): Resource<EmptyResponse>
}