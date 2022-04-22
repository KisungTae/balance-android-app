package com.beeswork.balance.ui.common

import androidx.lifecycle.viewModelScope
import com.beeswork.balance.domain.usecase.main.SaveLocationUseCase
import kotlinx.coroutines.launch

abstract class BaseLocationViewModel(
    private val saveLocationUseCase: SaveLocationUseCase
): BaseViewModel() {

    fun saveLocation(latitude: Double, longitude: Double, syncLocation: Boolean) {
        viewModelScope.launch {
            saveLocationUseCase.invoke(latitude, longitude, syncLocation)
        }
    }
}