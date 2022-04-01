package com.beeswork.balance.ui.registeractivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.domain.usecase.main.SaveLocationPermissionUseCase
import com.beeswork.balance.domain.usecase.main.SaveLocationUseCase

class RegisterViewModelFactory(
    private val saveLocationUseCase: SaveLocationUseCase,
    private val saveLocationPermissionUseCase: SaveLocationPermissionUseCase
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RegisterViewModel(saveLocationUseCase, saveLocationPermissionUseCase) as T
    }
}