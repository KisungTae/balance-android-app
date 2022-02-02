package com.beeswork.balance.ui.click

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.beeswork.balance.data.database.entity.click.Click
import com.beeswork.balance.data.database.repository.click.ClickRepository
import com.beeswork.balance.data.network.rds.click.ClickRDS
import com.beeswork.balance.internal.exception.ServerException
import retrofit2.HttpException
import java.io.IOException
import java.lang.RuntimeException


@ExperimentalPagingApi
class ClickRemoteMediator(
    private val clickRepository: ClickRepository
) : RemoteMediator<Int, Click>() {
    override suspend fun load(loadType: LoadType, state: PagingState<Int, Click>): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> {
                    return MediatorResult.Success(false)
                }
                LoadType.PREPEND -> {
                    return MediatorResult.Success(true)
                }
                LoadType.APPEND -> {
                    state.lastItemOrNull()?.swiperId
                }
            }

            val pageSize = state.config.pageSize
            val response = clickRepository.fetchClicks(state.config.pageSize, loadKey)
            if (response.isError()) {
                val exception = response.exception ?: RuntimeException()
                return MediatorResult.Error(exception)
            }
            val fetchedClickSize = response.data ?: 0
            return MediatorResult.Success(fetchedClickSize < pageSize)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }


    }
}