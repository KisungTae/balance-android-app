package com.beeswork.balance.domain.usecase.card

import com.beeswork.balance.data.database.result.ClickResult
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.match.ClickResponse
import java.util.*

interface ClickUseCase {

    suspend fun invoke(swipedId: UUID, answers: Map<Int, Boolean>): Resource<ClickResult>
}