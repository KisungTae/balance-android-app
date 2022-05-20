package com.beeswork.balance.ui.accountfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.login.LoginRepository
import com.beeswork.balance.data.database.repository.photo.PhotoRepository
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.domain.usecase.account.FetchProfileUseCase
import com.beeswork.balance.domain.usecase.account.GetProfilePhotoFlowUseCase
import com.beeswork.balance.domain.usecase.login.GetEmailUseCase
import com.beeswork.balance.internal.mapper.profile.ProfileMapper
import com.beeswork.balance.internal.provider.preference.PreferenceProvider

class AccountViewModelFactory(
    private val fetchProfileUseCase: FetchProfileUseCase,
    private val getEmailUseCase: GetEmailUseCase,
    private val getProfilePhotoFlowUseCase: GetProfilePhotoFlowUseCase,
    private val profileMapper: ProfileMapper,
    private val preferenceProvider: PreferenceProvider
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AccountViewModel(fetchProfileUseCase, getEmailUseCase, getProfilePhotoFlowUseCase, profileMapper, preferenceProvider) as T
    }
}