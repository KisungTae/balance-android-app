package com.beeswork.balance.data.database.repository.match

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.PagingSource
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.constant.LoadType
import org.threeten.bp.OffsetDateTime

interface MatchRepository {

    val fetchMatchesLiveData: LiveData<Resource<EmptyResponse>>

    suspend fun getMatch(chatId: Long): Match?
    suspend fun loadMatches(loadSize: Int, startPosition: Int): List<Match>
    suspend fun loadMatches(loadSize: Int, startPosition: Int, searchKeyword: String): List<Match>
    suspend fun fetchMatches()


    fun testFunction()
}