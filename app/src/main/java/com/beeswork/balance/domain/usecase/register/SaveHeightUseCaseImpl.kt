package com.beeswork.balance.domain.usecase.register

import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.exception.GenderNotSelectedException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class SaveHeightUseCaseImpl(
    private val profileRepository: ProfileRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : SaveHeightUseCase {

    override suspend fun invoke(height: Int): Resource<EmptyResponse> {
        return try {
            withContext(defaultDispatcher) {
                profileRepository.saveHeight(height)
                return@withContext Resource.success(EmptyResponse())
            }
        } catch (e: IOException) {
            Resource.error(e)
        }
    }
}