package com.beeswork.balance.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.domain.usecase.location.SaveLocationUseCase
import com.beeswork.balance.domain.usecase.location.UpdateLocationGrantedUseCase

class BaseLocationViewModelFactory(
    private val saveLocationUseCase: SaveLocationUseCase,
    private val updateLocationGrantedUseCase: UpdateLocationGrantedUseCase
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BaseLocationViewModel(saveLocationUseCase, updateLocationGrantedUseCase) as T
    }
}