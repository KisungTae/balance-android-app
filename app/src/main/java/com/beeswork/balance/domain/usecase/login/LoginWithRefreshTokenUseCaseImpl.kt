package com.beeswork.balance.domain.usecase.login

import com.beeswork.balance.data.database.repository.card.CardRepository
import com.beeswork.balance.data.database.repository.login.LoginRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.login.LoginDTO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class LoginWithRefreshTokenUseCaseImpl(
    private val loginRepository: LoginRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : LoginWithRefreshTokenUseCase {

    override suspend fun invoke(): Resource<LoginDTO> {
        return try {
            withContext(defaultDispatcher) {
                loginRepository.loginWithRefreshToken()
            }
        } catch (e: IOException) {
            Resource.error(e)
        }
    }


}