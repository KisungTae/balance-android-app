package com.beeswork.balance.ui.mainactivity

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.data.database.repository.main.MainRepository
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.ui.common.BaseViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainViewModel(
    private val mainRepository: MainRepository,
    private val settingRepository: SettingRepository
) : BaseViewModel() {

    //  TODO: change livedata to channel consumeAsFlow, and validateAccount() in onEach()
//    val webSocketEventLiveData = stompClient.webSocketEventLiveData

    val webSocketEventLiveData by viewModelLazyDeferred {
        mainRepository.webSocketEventFlow.asLiveData()
    }



    fun connectStomp() {
        viewModelScope.launch { mainRepository.connectStomp() }
    }

    fun disconnectStomp() {
        viewModelScope.launch { mainRepository.disconnectStomp() }
    }

    fun saveLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch { settingRepository.saveLocation(latitude, longitude) }
    }

    fun saveLocationPermissionResult(granted: Boolean) {
        viewModelScope.launch { settingRepository.saveLocationPermissionResult(granted) }
    }

    fun test() {
    }

}