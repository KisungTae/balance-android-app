package com.beeswork.balance.domain.usecase.register

import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.exception.GenderNotSelectedException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneOffset
import java.io.IOException

class SaveGenderUseCaseImpl(
    private val profileRepository: ProfileRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : SaveGenderUseCase {

    override suspend fun invoke(gender: Boolean?): Resource<EmptyResponse> {
        return try {
            withContext(defaultDispatcher) {
                if (gender == null) {
                    return@withContext Resource.error(GenderNotSelectedException())
                }
                profileRepository.saveGender(gender)
                return@withContext Resource.success(EmptyResponse())
            }
        } catch (e: IOException) {
            Resource.error(e)
        }
    }
}