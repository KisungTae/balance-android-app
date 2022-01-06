package com.beeswork.balance.ui.registeractivity.gender

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.ui.registeractivity.name.NameViewModel

class GenderViewModelFactory (
    private val profileRepository: ProfileRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return GenderViewModel(profileRepository) as T
    }
}