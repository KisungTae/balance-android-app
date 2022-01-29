package com.beeswork.balance.ui.click

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.beeswork.balance.data.database.entity.click.Click
import com.beeswork.balance.data.database.repository.click.ClickRepository
import com.beeswork.balance.data.network.rds.click.ClickRDS
import retrofit2.HttpException
import java.io.IOException
import java.lang.NullPointerException


@ExperimentalPagingApi
class ClickRemoteMediator(
    private val clickRepository: ClickRepository
) : RemoteMediator<Int, Click>() {
    override suspend fun load(loadType: LoadType, state: PagingState<Int, Click>): MediatorResult {
        return try {
            println("clickRemoteMediator load() | loadType: $loadType")


            val loadKey = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull() ?: return MediatorResult.Success(endOfPaginationReached = true)
                    lastItem.swiperId
                }
            }




            return MediatorResult.Success(true)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }


    }
}