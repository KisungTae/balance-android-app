package com.beeswork.balance.ui.match

import androidx.paging.PositionalDataSource
import com.beeswork.balance.data.database.entity.Match
import com.beeswork.balance.data.database.repository.match.MatchRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MatchDataSource(
    private val matchRepository: MatchRepository,
    private val scope: CoroutineScope,
    private val keyword: String
) : PositionalDataSource<Match>() {

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Match>) {
        scope.launch {
            val matches = loadMatches(params.requestedLoadSize, params.requestedStartPosition, keyword)
            callback.onResult(matches ?: listOf(), 0)
        }
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Match>) {
        scope.launch {
            callback.onResult(loadMatches(params.loadSize, params.startPosition, keyword) ?: listOf())
        }
    }

    private suspend fun loadMatches(loadSize: Int, startPosition: Int, keyword: String): List<Match>? {
        return if (keyword.isEmpty()) matchRepository.loadMatches(loadSize, startPosition)
        else matchRepository.loadMatches(loadSize, startPosition, keyword)
    }
}