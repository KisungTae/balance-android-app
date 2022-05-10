package com.beeswork.balance.data.database.repository

import com.beeswork.balance.data.network.rds.login.LoginRDS
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.login.RefreshAccessTokenDTO
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.internal.exception.AccessTokenNotFoundException
import com.beeswork.balance.internal.exception.RefreshTokenNotFoundException
import com.beeswork.balance.internal.provider.preference.PreferenceProvider

abstract class BaseRepository(
    protected val loginRDS: LoginRDS,
    protected val preferenceProvider: PreferenceProvider
) {


    suspend fun<T> getResponse(block: suspend() -> Resource<T>): Resource<T> {
        val response = block.invoke()

        when {
            response.isSuccess() -> {
                return response
            }
            response.isError() && response.isExceptionCodeEqualTo(ExceptionCode.EXPIRED_JWT_EXCEPTION) -> {
                val refreshAccessTokenResponse = doRefreshAccessToken()
                if (refreshAccessTokenResponse.isSuccess()) {
                    return block.invoke()
                }
                return response
            }
            else -> {
                return response
            }
        }
    }

    protected suspend fun doRefreshAccessToken(): Resource<RefreshAccessTokenDTO> {
        val accessToken = preferenceProvider.getAccessToken()
        if (accessToken.isNullOrBlank()) {
            return Resource.error(AccessTokenNotFoundException())
        }

        val refreshToken = preferenceProvider.getRefreshToken()
        if (refreshToken.isNullOrBlank()) {
            return Resource.error(RefreshTokenNotFoundException())
        }

        val response = loginRDS.refreshAccessToken(accessToken, refreshToken)
        response.data?.let { refreshAccessTokenDTO ->
            preferenceProvider.putAccessInfo(refreshAccessTokenDTO.accessToken, refreshAccessTokenDTO.refreshToken)
        }
        return response
    }
}