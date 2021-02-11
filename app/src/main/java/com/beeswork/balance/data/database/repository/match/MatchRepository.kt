package com.beeswork.balance.data.database.repository.match

import androidx.paging.DataSource
import com.beeswork.balance.data.database.entity.Match

interface MatchRepository {
    suspend fun fetchMatches()
    suspend fun getMatches(): DataSource.Factory<Int, Match>
}