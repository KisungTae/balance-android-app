package com.beeswork.balance.data.database.repository.match

import androidx.paging.DataSource
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.LoadType
import org.threeten.bp.OffsetDateTime

interface MatchRepository {
    suspend fun fetchMatches(): Resource<EmptyResponse>
    suspend fun loadMoreMatches(pageSize: Int, pivotChatId: Long, loadType: LoadType)


    fun testFunction()
}