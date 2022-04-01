package com.beeswork.balance.ui.registeractivity.registerfinish

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.domain.usecase.register.SaveProfileUseCase
import com.beeswork.balance.ui.registeractivity.name.NameViewModel

class RegisterFinishViewModelFactory (
    private val saveProfileUseCase: SaveProfileUseCase
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RegisterFinishViewModel(saveProfileUseCase) as T
    }
}