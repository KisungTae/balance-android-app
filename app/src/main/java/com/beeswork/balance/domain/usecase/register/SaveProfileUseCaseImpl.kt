package com.beeswork.balance.domain.usecase.register

import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.exception.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class SaveProfileUseCaseImpl(
    private val profileRepository: ProfileRepository,
    private val settingRepository: SettingRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : SaveProfileUseCase {

    override suspend fun invoke(): Resource<EmptyResponse> {
        return try {
            withContext(defaultDispatcher) {
                val location = settingRepository.getLocation() ?: return@withContext Resource.error(LocationNotFoundException())
                val profile = profileRepository.getProfile()
                when {
                    profile?.name == null -> {
                        return@withContext Resource.error(NameEmptyException())
                    }
                    profile.gender == null -> {
                        return@withContext Resource.error(GenderNotSelectedException())
                    }
                    profile.birthDate == null -> {
                        return@withContext Resource.error(BirthDateNotSavedException())
                    }
                    else -> {
                        return@withContext profileRepository.saveProfile(
                            profile.name,
                            profile.gender,
                            profile.birthDate,
                            profile.height,
                            profile.about,
                            location.latitude,
                            location.longitude
                        )
                    }
                }
            }
        } catch (e: IOException) {
            Resource.error(e)
        }
    }

}