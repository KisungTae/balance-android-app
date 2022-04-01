package com.beeswork.balance.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.domain.usecase.main.SaveLocationPermissionUseCase
import com.beeswork.balance.domain.usecase.main.SaveLocationUseCase
import kotlinx.coroutines.launch

abstract class BaseLocationViewModel(
    private val saveLocationUseCase: SaveLocationUseCase,
    private val saveLocationPermissionUseCase: SaveLocationPermissionUseCase
): ViewModel() {

    fun saveLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            saveLocationUseCase.invoke(latitude, longitude)
        }
    }

    fun saveLocationPermissionResult(granted: Boolean) {
        viewModelScope.launch {
            saveLocationPermissionUseCase.invoke(granted)
        }
    }
}