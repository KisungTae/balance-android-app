package com.beeswork.balance.data.database.repository.match

import androidx.paging.DataSource
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.LoadType
import org.threeten.bp.OffsetDateTime

interface MatchRepository {
    suspend fun prependMatches(pageSize: Int, chatId: Long): List<Match>
    suspend fun appendMatches(pageSize: Int, chatId: Long): List<Match>
    suspend fun fetchMatches(): Resource<EmptyResponse>


    fun testFunction()
}