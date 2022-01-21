package com.beeswork.balance.ui.click

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.beeswork.balance.data.database.entity.click.Click
import retrofit2.HttpException
import java.io.IOException
import java.lang.NullPointerException


//class ClickRemoteMediator(
//
//): RemoteMediator<Int, Click>() {
//    override suspend fun load(loadType: LoadType, state: PagingState<Int, Click>): MediatorResult {
//        return try {
//
//
//            println("clickRemoteMediator load() | loadType: $loadType")
//
//            val loadKey: Int? = when (loadType) {
//                LoadType.REFRESH -> null
//                LoadType.PREPEND -> return MediatorResult.Success(true)
//                LoadType.APPEND -> {
//
//                }
//            }
//
//            return MediatorResult.Success(true)
//        } catch (e: IOException) {
//            MediatorResult.Error(e)
//        } catch (e: HttpException) {
//            MediatorResult.Error(e)
//        }
//
//
//    }
//}