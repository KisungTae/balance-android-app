package com.beeswork.balance.data.database.repository.login

import com.beeswork.balance.data.database.dao.FCMTokenDAO
import com.beeswork.balance.data.database.dao.LoginDAO
import com.beeswork.balance.data.database.entity.login.Login
import com.beeswork.balance.data.database.repository.BaseRepository
import com.beeswork.balance.data.network.rds.login.LoginRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.data.network.response.login.LoginDTO
import com.beeswork.balance.data.network.response.login.RefreshAccessTokenDTO
import com.beeswork.balance.internal.constant.LoginType
import com.beeswork.balance.internal.exception.AccessTokenNotFoundException
import com.beeswork.balance.internal.exception.AccountIdNotFoundException
import com.beeswork.balance.internal.exception.RefreshTokenNotFoundException
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.lang.NullPointerException
import java.util.*

class LoginRepositoryImpl(
    private val loginDAO: LoginDAO,
    private val fcmTokenDAO: FCMTokenDAO,
    loginRDS: LoginRDS,
    preferenceProvider: PreferenceProvider,
    private val ioDispatcher: CoroutineDispatcher
) : BaseRepository(loginRDS, preferenceProvider), LoginRepository {

    private suspend fun saveEmail(accountId: UUID, email: String?, loginType: LoginType) {
        withContext(ioDispatcher) {
            val login = Login(accountId, loginType, email, true)
            loginDAO.insert(login)
        }
    }

    override suspend fun saveEmail(email: String): Resource<String> {
        return withContext(ioDispatcher) {
            val accountId = preferenceProvider.getAccountId() ?: return@withContext Resource.error(AccountIdNotFoundException())

            loginDAO.updateSyncedBy(accountId, false)
            val response = loginRDS.saveEmail(email)

            if (response.isSuccess()) {
                loginDAO.updateEmailBy(accountId, email)
                return@withContext response.map { null }
            } else {
                loginDAO.updateSyncedBy(accountId, true)
                return@withContext response.map { loginDAO.getEmailBy(accountId) }
            }
        }
    }

    override suspend fun getEmail(): String? {
        return withContext(ioDispatcher) {
            return@withContext loginDAO.getEmailBy(preferenceProvider.getAccountId())
        }
    }


    override suspend fun fetchEmail(): Resource<String> {
        return withContext(ioDispatcher) {
            val accountId = preferenceProvider.getAccountId() ?: return@withContext Resource.error(AccountIdNotFoundException())
            val response = loginRDS.fetchEmail()
            if (response.isSuccess()) loginDAO.updateEmailBy(accountId, response.data)
            return@withContext response
        }
    }

    override suspend fun socialLogin(loginId: String, accessToken: String, loginType: LoginType): Resource<LoginDTO> {
        return withContext(ioDispatcher) {
            val response = loginRDS.socialLogin(loginId, accessToken, loginType, fcmTokenDAO.getById())
            response.data?.let { loginDTO ->
                saveEmail(loginDTO.accountId, loginDTO.email, loginType)
                preferenceProvider.putLoginInfo(
                    loginDTO.accountId,
                    loginDTO.accessToken,
                    loginDTO.refreshToken,
                    loginDTO.photoBucketUrl
                )
            }
            return@withContext response
        }
    }

    override suspend fun login(): Resource<EmptyResponse> {
//      todo: implement it
        return Resource.error(NullPointerException())
    }

    override suspend fun deleteLogin() {
        withContext(ioDispatcher) {
            loginDAO.deleteBy(preferenceProvider.getAccountId())
        }
    }

    override suspend fun isEmailSynced(): Boolean {
        return withContext(ioDispatcher) {
            return@withContext loginDAO.isSyncedBy(preferenceProvider.getAccountId()) ?: false
        }
    }

    override suspend fun getLoginType(): LoginType? {
        return withContext(ioDispatcher) {
            return@withContext loginDAO.getLoginTypeBy(preferenceProvider.getAccountId())
        }
    }

    override suspend fun loginWithRefreshToken(): Resource<LoginDTO> {
        return withContext(ioDispatcher) {
            val accessToken = preferenceProvider.getAccessToken()
            if (accessToken.isNullOrBlank()) {
                return@withContext Resource.error(AccessTokenNotFoundException())
            }

            val refreshToken = preferenceProvider.getRefreshToken()
            if (refreshToken.isNullOrBlank()) {
                return@withContext Resource.error(RefreshTokenNotFoundException())
            }

            val response = loginRDS.loginWithRefreshToken(accessToken, refreshToken, fcmTokenDAO.getById())
            response.data?.let { loginDTO ->
                preferenceProvider.putLoginInfo(
                    loginDTO.accountId,
                    loginDTO.accessToken,
                    loginDTO.refreshToken,
                    loginDTO.photoBucketUrl
                )
            }
            return@withContext response
        }
    }

    override suspend fun refreshAccessToken(): Resource<RefreshAccessTokenDTO> {
        return withContext(ioDispatcher) {
            return@withContext super.doRefreshAccessToken()
        }
    }

    override fun getEmailFlow(): Flow<String?> {
        return loginDAO.getEmailFlowBy(preferenceProvider.getAccountId())
    }
}