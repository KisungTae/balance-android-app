package com.beeswork.balance.domain.usecase.main

import com.beeswork.balance.data.database.repository.main.MainRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ConnectToStompUseCaseImpl(
    private val mainRepository: MainRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
): ConnectToStompUseCase {

    override suspend fun invoke(forceToConnect: Boolean) = withContext(defaultDispatcher) {
        mainRepository.connectStomp(forceToConnect)
    }
}