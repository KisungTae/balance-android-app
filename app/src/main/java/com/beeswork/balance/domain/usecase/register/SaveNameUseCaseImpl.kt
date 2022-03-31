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

class SaveNameUseCaseImpl(
    private val profileRepository: ProfileRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : SaveNameUseCase {

    override suspend fun invoke(name: String): Resource<EmptyResponse> = withContext(defaultDispatcher) {
        try {
            when {
                name.isBlank() -> {
                    return@withContext Resource.error<EmptyResponse>(NameEmptyException())
                }
                name.toByteArray().size > MAX_NAME_SIZE -> {
                    return@withContext Resource.error<EmptyResponse>(NameMaxSizeExceedException())
                }
                else -> {
                    return@withContext profileRepository.saveName(name)
                }
            }
        } catch (e: IOException) {
            Resource.error(e)
        }
    }

    companion object {
        const val MAX_NAME_SIZE = 50
    }
}