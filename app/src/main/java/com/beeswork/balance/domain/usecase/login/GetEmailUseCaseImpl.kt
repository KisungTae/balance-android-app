package com.beeswork.balance.domain.usecase.login

import com.beeswork.balance.data.database.repository.login.LoginRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetEmailUseCaseImpl(
    private val loginRepository: LoginRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : GetEmailUseCase {

    override suspend fun invoke(): String? = withContext(defaultDispatcher) {
        return@withContext loginRepository.getEmail()
    }
}