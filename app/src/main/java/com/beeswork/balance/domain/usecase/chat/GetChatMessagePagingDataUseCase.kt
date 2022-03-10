package com.beeswork.balance.domain.usecase.chat

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.beeswork.balance.domain.uistate.chat.ChatMessageItemUIState
import kotlinx.coroutines.CoroutineScope
import java.util.*

interface GetChatMessagePagingDataUseCase {
    fun invoke(chatId: UUID, scope: CoroutineScope): LiveData<PagingData<ChatMessageItemUIState>>
}