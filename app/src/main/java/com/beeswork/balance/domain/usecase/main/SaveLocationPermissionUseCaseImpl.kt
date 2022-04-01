package com.beeswork.balance.domain.usecase.main

import com.beeswork.balance.data.database.repository.setting.SettingRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SaveLocationPermissionUseCaseImpl(
    private val settingRepository: SettingRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
): SaveLocationPermissionUseCase {

    override suspend fun invoke(granted: Boolean) =  withContext(defaultDispatcher){
        settingRepository.saveLocationPermissionResult(granted)
    }
}