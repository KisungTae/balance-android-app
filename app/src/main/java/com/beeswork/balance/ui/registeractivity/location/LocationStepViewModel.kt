package com.beeswork.balance.ui.registeractivity.location

import androidx.lifecycle.asLiveData
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.internal.constant.LocationPermissionStatus
import com.beeswork.balance.ui.common.BaseViewModel
import kotlinx.coroutines.flow.map

class LocationStepViewModel(
    private val settingRepository: SettingRepository
): BaseViewModel() {

    val locationGrantedLiveData by viewModelLazyDeferred {
        settingRepository.getLocationPermissionStatusFlow().map { locationPermissionStatus ->
            locationPermissionStatus ?: LocationPermissionStatus.CHECKING
        }.asLiveData()
    }
}