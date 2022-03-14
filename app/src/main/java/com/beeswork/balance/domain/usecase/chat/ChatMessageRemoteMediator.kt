package com.beeswork.balance.domain.usecase.chat

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.beeswork.balance.data.database.entity.chat.ChatMessage
import com.beeswork.balance.data.database.repository.chat.ChatRepository
import retrofit2.HttpException
import java.io.IOException
import java.util.*

@ExperimentalPagingApi
class ChatMessageRemoteMediator(
    private val chatRepository: ChatRepository,
    private val chatId: UUID
) : RemoteMediator<Int, ChatMessage>() {
    override suspend fun load(loadType: LoadType, state: PagingState<Int, ChatMessage>): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> {
                    return MediatorResult.Success(false)
                }
                LoadType.PREPEND -> {
                    return MediatorResult.Success(true)
                }
                LoadType.APPEND -> {
                    state.lastItemOrNull()?.id
                }
            }
            val pageSize = state.config.pageSize
            val response = chatRepository.fetchChatMessages(chatId, loadKey, pageSize)
            if (response.isError()) {
                val exception = response.exception ?: IOException()
                return MediatorResult.Error(exception)
            }
            return MediatorResult.Success((response.data?.size ?: 0) < pageSize)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}