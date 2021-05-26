package com.beeswork.balance.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.internal.mapper.profile.ProfileMapper

class ProfileViewModelFactory(
    private val profileRepository: ProfileRepository,
    private val profileMapper: ProfileMapper
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfileViewModel(profileRepository, profileMapper) as T
    }

}