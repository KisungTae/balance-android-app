package com.beeswork.balance.data.database.repository.match

import androidx.paging.DataSource
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse

interface MatchRepository {
    suspend fun listMatches(): DataSource.Factory<Int, Match>
    suspend fun fetchMatches(): Resource<EmptyResponse>
}