package com.beeswork.balance.ui.swipefragment

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.beeswork.balance.data.database.entity.swipe.Swipe
import com.beeswork.balance.data.database.repository.swipe.SwipeRepository
import retrofit2.HttpException
import java.io.IOException


@ExperimentalPagingApi
class SwipeRemoteMediator(
    private val swipeRepository: SwipeRepository
) : RemoteMediator<Int, Swipe>() {
    override suspend fun load(loadType: LoadType, state: PagingState<Int, Swipe>): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> {
                    return MediatorResult.Success(false)
                }
                LoadType.PREPEND -> {
                    return MediatorResult.Success(false)
                }
                LoadType.APPEND -> {
                    state.lastItemOrNull()?.id
                }
            }

            val pageSize = state.config.pageSize
//            val response = swipeRepository.fetchSwipes(pageSize, loadKey)
//            if (response.isError()) {
//                val exception = response.exception ?: IOException()
//                return MediatorResult.Error(exception)
//            }
//            return MediatorResult.Success((response.data?.swipeDTOs?.size ?: 0) < pageSize)
            return MediatorResult.Success(false)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }


    }
}