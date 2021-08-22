package com.beeswork.balance.ui.registeractivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.photo.PhotoRepository
import com.beeswork.balance.data.database.repository.profile.ProfileRepository

class RegisterViewModelFactory(
    private val profileRepository: ProfileRepository,
    private val photoRepository: PhotoRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RegisterViewModel(profileRepository, photoRepository) as T
    }
}