package com.beeswork.balance.data.database.repository.login

import com.beeswork.balance.data.network.rds.login.LoginRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.login.LoginDTO
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class LoginRepositoryImpl(
    private val preferenceProvider: PreferenceProvider,
    private val loginRDS: LoginRDS,
    private val ioDispatcher: CoroutineDispatcher
) : LoginRepository {
    override suspend fun socialLogin(loginId: String, accessToken: String): Resource<LoginDTO> {
        return withContext(ioDispatcher) {
            val response = loginRDS.socialLogin(
                preferenceProvider.getAccountId(),
                preferenceProvider.getIdentityToken(),
                loginId,
                accessToken
            )
            if (response.isSuccess()) response.data?.let { data ->
                println("data: ================")
                println(data)
            }
            return@withContext response
        }
    }

    override suspend fun login(): Resource<EmptyResponse> {
        return Resource.error("error")
    }

}