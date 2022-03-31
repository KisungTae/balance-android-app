package com.beeswork.balance.domain.usecase.register

import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class SaveGenderUseCaseImpl(
    private val profileRepository: ProfileRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : SaveGenderUseCase {

    override suspend fun invoke(gender: Boolean): Resource<EmptyResponse> = withContext(defaultDispatcher) {
        try {
            profileRepository.saveGender(gender)
            return@withContext Resource.success(EmptyResponse())
        } catch (e: IOException) {
            return@withContext Resource.error(e)
        }
    }
}