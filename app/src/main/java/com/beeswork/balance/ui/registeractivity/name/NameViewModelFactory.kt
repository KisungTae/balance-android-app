package com.beeswork.balance.ui.registeractivity.name

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.domain.usecase.register.GetNameUseCase
import com.beeswork.balance.domain.usecase.register.SaveNameUseCase

class NameViewModelFactory(
    private val getNameUseCase: GetNameUseCase,
    private val saveNameUseCase: SaveNameUseCase
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NameViewModel(getNameUseCase, saveNameUseCase) as T
    }
}