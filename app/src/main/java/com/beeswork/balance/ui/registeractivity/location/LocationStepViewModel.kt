package com.beeswork.balance.ui.registeractivity.location

import androidx.lifecycle.asLiveData
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.ui.common.BaseViewModel

class LocationStepViewModel(
    private val settingRepository: SettingRepository
): BaseViewModel() {

    val locationGrantedLiveData by viewModelLazyDeferred {
        settingRepository.getLocationGrantedFlow().asLiveData()
    }
}