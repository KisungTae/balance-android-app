package com.beeswork.balance.domain.usecase.chat

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.match.UnmatchDTO
import java.util.*

interface UnmatchUseCase {
    suspend fun onInvoke(chatId: UUID, swipedId: UUID): Resource<UnmatchDTO>
}