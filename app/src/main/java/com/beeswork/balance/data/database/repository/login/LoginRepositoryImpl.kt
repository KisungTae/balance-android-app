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

    override suspend fun saveEmail(email: String): Resource<String> {
        return withContext(ioDispatcher) {
            val accountId = preferenceProvider.getAccountId()
            loginDAO.updateSynced(accountId, false)
            val response = loginRDS.saveEmail(accountId, preferenceProvider.getIdentityToken(), email)

            if (response.isSuccess()) {
                loginDAO.updateEmail(accountId, email)
                return@withContext response.map { null }
            } else {
                loginDAO.updateSynced(accountId, true)
                return@withContext response.map { loginDAO.findEmail(accountId) }
            }
        }
    }

    override suspend fun getEmail(): String? {
        return withContext(ioDispatcher) {
            return@withContext loginDAO.findEmail(preferenceProvider.getAccountId())
        }
    }


    override suspend fun fetchEmail(): Resource<String> {
        return withContext(ioDispatcher) {
            val response = loginRDS.fetchEmail(
                preferenceProvider.getAccountId(),
                preferenceProvider.getIdentityToken()
            )
            if (response.isSuccess()) loginDAO.updateEmail(preferenceProvider.getAccountId(), response.data)
            return@withContext response
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

    override suspend fun isEmailSynced(): Boolean {
        return withContext(ioDispatcher) {
            return@withContext loginDAO.isSynced(preferenceProvider.getAccountId()) ?: false
        }
    }

    override suspend fun getLoginType(): LoginType {
        return withContext(ioDispatcher) {
            return@withContext loginDAO.findLoginType(preferenceProvider.getAccountId())
        }
    }

    override fun getEmailFlow(): Flow<String?> {
        return loginDAO.findEmailAsFlow(preferenceProvider.getAccountId())
    }



}