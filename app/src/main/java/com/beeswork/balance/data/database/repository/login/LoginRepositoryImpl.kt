package com.beeswork.balance.data.database.repository.login

import com.beeswork.balance.data.database.dao.LoginDAO
import com.beeswork.balance.data.database.entity.Login
import com.beeswork.balance.data.network.rds.login.LoginRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.login.LoginDTO
import com.beeswork.balance.internal.constant.LoginType
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.*

class LoginRepositoryImpl(
    private val preferenceProvider: PreferenceProvider,
    private val loginDAO: LoginDAO,
    private val loginRDS: LoginRDS,
    private val ioDispatcher: CoroutineDispatcher
) : LoginRepository {
    override suspend fun saveEmail(email: String?, loginType: LoginType) {
        withContext(ioDispatcher) {
            val login = Login(preferenceProvider.getAccountId(), loginType, email, true)
            loginDAO.insert(login)
        }
    }

    override suspend fun fetchEmail(): Resource<EmptyResponse> {
        return withContext(ioDispatcher) {
            val isSynced = loginDAO.isSynced(preferenceProvider.getAccountId()) ?: false
            if (!isSynced) {
                val response = loginRDS.fetchEmail(
                    preferenceProvider.getAccountId(),
                    preferenceProvider.getIdentityToken()
                )
                response.data?.let { email ->
                    loginDAO.updateEmail(preferenceProvider.getAccountId(), email)
                }
                return@withContext response.toEmptyResponse()
            } else return@withContext Resource.success(EmptyResponse())
        }
    }


    override suspend fun socialLogin(loginId: String, accessToken: String, loginType: LoginType): Resource<LoginDTO> {
        return withContext(ioDispatcher) {
            val response = loginRDS.socialLogin(loginId, accessToken, loginType)
            if (response.isSuccess()) response.data?.let { data ->
                preferenceProvider.putAccountId(data.accountId)
                preferenceProvider.putIdentityTokenId(data.identityToken)
                preferenceProvider.putJwtToken(data.jwtToken)
            }
            return@withContext response
        }
    }

    override suspend fun login(): Resource<EmptyResponse> {
        return Resource.error("error")
    }

    override suspend fun deleteLogin() {
        withContext(ioDispatcher) {
            loginDAO.deleteByAccountId(preferenceProvider.getAccountId())
        }
    }

    override fun getEmailFlow(): Flow<String?> {
        return loginDAO.findEmailAsFlow(preferenceProvider.getAccountId())
    }


}