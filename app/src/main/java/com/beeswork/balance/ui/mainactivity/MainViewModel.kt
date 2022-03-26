package com.beeswork.balance.ui.mainactivity

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.data.database.repository.main.MainRepository
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.data.network.service.stomp.WebSocketEvent
import com.beeswork.balance.domain.uistate.main.WebSocketEventUIState
import com.beeswork.balance.internal.constant.ExceptionCode
import com.beeswork.balance.ui.common.BaseViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainViewModel(
    private val mainRepository: MainRepository,
    private val settingRepository: SettingRepository,
    private val defaultDispatcher: CoroutineDispatcher
) : BaseViewModel() {

    val webSocketEventUIStateLiveData by viewModelLazyDeferred {
        mainRepository.webSocketEventFlow.map { webSocketEvent ->
            WebSocketEventUIState(ExceptionCode.isLoginException(webSocketEvent.exception), webSocketEvent.exception)
        }.asLiveData(viewModelScope.coroutineContext + defaultDispatcher)
    }

    fun connectStomp() {
        viewModelScope.launch { mainRepository.connectStomp(true) }
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