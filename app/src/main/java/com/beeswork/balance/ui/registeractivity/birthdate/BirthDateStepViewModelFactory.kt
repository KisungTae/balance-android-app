package com.beeswork.balance.ui.registeractivity.birthdate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.domain.usecase.register.GetBirthDateUseCase
import com.beeswork.balance.domain.usecase.register.SaveBirthDateUseCase

class BirthDateStepViewModelFactory (
    private val getBirthDateUseCase: GetBirthDateUseCase,
    private val saveBirthDateUseCase: SaveBirthDateUseCase
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BirthDateStepViewModel(getBirthDateUseCase, saveBirthDateUseCase) as T
    }
}