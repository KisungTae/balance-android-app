package com.beeswork.balance.domain.usecase.account

import com.beeswork.balance.data.database.entity.profile.Profile
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class FetchProfileUseCaseImpl(
    private val profileRepository: ProfileRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
): FetchProfileUseCase {

    override suspend fun invoke(sync: Boolean): Resource<Profile> {
        return try {
            withContext(defaultDispatcher) {
                return@withContext profileRepository.fetchProfile(sync)
            }
        } catch (e: IOException) {
            return Resource.error(e)
        }
    }
}