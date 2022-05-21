package com.beeswork.balance.domain.usecase.profile

import com.beeswork.balance.data.database.entity.profile.Profile
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.IOException

class SaveBioUseCaseImpl(
    private val profileRepository: ProfileRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : SaveBioUseCase {

    override suspend fun invoke(height: Int?, about: String?): Resource<EmptyResponse> {
        return try {
            withContext(defaultDispatcher) {
                delay(10000)
                return@withContext profileRepository.saveBio(height, about)
            }
        } catch (e: IOException) {
            return Resource.error(e)
        }
    }
}