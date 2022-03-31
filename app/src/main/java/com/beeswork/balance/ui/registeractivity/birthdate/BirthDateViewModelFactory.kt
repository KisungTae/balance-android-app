package com.beeswork.balance.ui.registeractivity.birthdate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.domain.usecase.register.GetBirthDateUseCase
import com.beeswork.balance.domain.usecase.register.SaveBirthDateUseCase
import com.beeswork.balance.ui.registeractivity.name.NameViewModel

class BirthDateViewModelFactory (
    private val getBirthDateUseCase: GetBirthDateUseCase,
    private val saveBirthDateUseCase: SaveBirthDateUseCase
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BirthDateViewModel(getBirthDateUseCase, saveBirthDateUseCase) as T
    }
}