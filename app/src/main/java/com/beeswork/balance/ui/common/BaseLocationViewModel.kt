package com.beeswork.balance.ui.common

import androidx.lifecycle.viewModelScope
import com.beeswork.balance.domain.usecase.location.SaveLocationUseCase
import com.beeswork.balance.domain.usecase.location.UpdateLocationGrantedUseCase
import com.beeswork.balance.internal.constant.LocationPermissionStatus
import kotlinx.coroutines.launch

class BaseLocationViewModel(
    private val saveLocationUseCase: SaveLocationUseCase,
    private val updateLocationGrantedUseCase: UpdateLocationGrantedUseCase
): BaseViewModel() {

    fun saveLocation(latitude: Double, longitude: Double, syncLocation: Boolean) {
        viewModelScope.launch {
            saveLocationUseCase.invoke(latitude, longitude, syncLocation)
        }
    }

    fun updateLocationPermissionStatus(locationPermissionStatus: LocationPermissionStatus) {
        viewModelScope.launch {
            updateLocationGrantedUseCase.invoke(locationPermissionStatus)
        }
    }


}