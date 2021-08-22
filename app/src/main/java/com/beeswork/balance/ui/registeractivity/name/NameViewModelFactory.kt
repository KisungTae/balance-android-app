package com.beeswork.balance.ui.registeractivity.name

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.profile.ProfileRepository

class NameViewModelFactory(
    private val profileRepository: ProfileRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NameViewModel(profileRepository) as T
    }
}