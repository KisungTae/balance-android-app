package com.beeswork.balance.ui.registeractivity.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.ui.registeractivity.name.NameViewModel

class AboutViewModelFactory (
    private val profileRepository: ProfileRepository
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AboutViewModel(profileRepository) as T
    }
}