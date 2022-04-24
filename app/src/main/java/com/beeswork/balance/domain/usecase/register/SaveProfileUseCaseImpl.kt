package com.beeswork.balance.domain.usecase.register

import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.exception.NameEmptyException
import com.beeswork.balance.internal.exception.NameMaxSizeExceedException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class SaveProfileUseCaseImpl(
    private val profileRepository: ProfileRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : SaveProfileUseCase {

    override suspend fun invoke(): Resource<EmptyResponse> {
        return try {
            withContext(defaultDispatcher) {
                return@withContext profileRepository.saveProfile()
            }
        } catch (e: IOException) {
            Resource.error(e)
        }
    }

}