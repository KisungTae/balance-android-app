package com.beeswork.balance.domain.usecase.main

import com.beeswork.balance.data.database.repository.main.MainRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DisconnectStompUseCaseImpl(
    private val mainRepository: MainRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : DisconnectStompUseCase {

    override suspend fun invoke() = withContext(defaultDispatcher) {
        mainRepository.disconnectStomp()
    }
}