package com.beeswork.balance.data.database.repository.login

import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay

class LoginRepositoryImpl(
    private val preferenceProvider: PreferenceProvider,
    private val ioDispatcher: CoroutineDispatcher
): LoginRepository {
    override suspend fun login(): Resource<EmptyResponse> {
        return Resource.error("error")
    }

}