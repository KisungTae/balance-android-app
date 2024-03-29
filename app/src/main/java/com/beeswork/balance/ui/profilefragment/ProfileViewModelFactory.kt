package com.beeswork.balance.ui.profilefragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.photo.PhotoRepository
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.domain.usecase.account.FetchProfileUseCase
import com.beeswork.balance.domain.usecase.profile.SaveBioUseCase
import com.beeswork.balance.internal.mapper.photo.PhotoMapper
import com.beeswork.balance.internal.mapper.profile.ProfileMapper
import kotlinx.coroutines.CoroutineDispatcher

class ProfileViewModelFactory(
    private val fetchProfileUseCase: FetchProfileUseCase,
    private val saveBioUseCase: SaveBioUseCase,
    private val profileMapper: ProfileMapper
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfileViewModel(fetchProfileUseCase, saveBioUseCase, profileMapper) as T
    }

}