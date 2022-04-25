package com.beeswork.balance.domain.usecase.login

import com.beeswork.balance.data.database.repository.login.LoginRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.login.LoginDTO
import com.beeswork.balance.internal.constant.LoginType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class SocialLoginUseCaseImpl(
    private val loginRepository: LoginRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : SocialLoginUseCase {

    override suspend fun invoke(loginId: String, accessToken: String, loginType: LoginType): Resource<LoginDTO> {
        return try {
            withContext(defaultDispatcher) {
                loginRepository.socialLogin(loginId, accessToken, loginType)
            }
        } catch (e: IOException) {
            Resource.error(e)
        }
    }
}