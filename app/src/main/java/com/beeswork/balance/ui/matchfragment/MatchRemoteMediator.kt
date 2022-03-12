package com.beeswork.balance.ui.matchfragment

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.beeswork.balance.data.database.entity.match.Match
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.internal.constant.MatchPageFilter
import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.IOException
import java.lang.RuntimeException

@ExperimentalPagingApi
class MatchRemoteMediator(
    private val matchRepository: MatchRepository,
    private val matchPageFilter: MatchPageFilter?
): RemoteMediator<Int, Match>() {
    override suspend fun load(loadType: LoadType, state: PagingState<Int, Match>): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> {
                    return MediatorResult.Success(false)
                }
                LoadType.PREPEND -> {
                    return MediatorResult.Success(true)
                }
                LoadType.APPEND -> {
                    state.lastItemOrNull()?.swipedId
                }
            }

            val pageSize = state.config.pageSize
            val response = matchRepository.fetchMatches(pageSize, loadKey, matchPageFilter)

            if (response.isError()) {
                val exception = response.exception ?: IOException()
                return MediatorResult.Error(exception)
            }
            return MediatorResult.Success((response.data?.matchDTOs?.size ?: 0) < pageSize)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}