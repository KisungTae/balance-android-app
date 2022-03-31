package com.beeswork.balance.ui.registeractivity.gender

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.domain.usecase.register.GetGenderUseCase
import com.beeswork.balance.domain.usecase.register.SaveGenderUseCase
import com.beeswork.balance.ui.registeractivity.name.NameViewModel

class GenderViewModelFactory (
    private val getGenderUseCase: GetGenderUseCase,
    private val saveGenderUseCase: SaveGenderUseCase
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return GenderViewModel(getGenderUseCase, saveGenderUseCase) as T
    }
}