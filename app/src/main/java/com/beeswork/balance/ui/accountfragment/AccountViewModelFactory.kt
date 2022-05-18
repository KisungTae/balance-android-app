package com.beeswork.balance.ui.accountfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.login.LoginRepository
import com.beeswork.balance.data.database.repository.photo.PhotoRepository
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.domain.usecase.account.FetchProfileUseCase
import com.beeswork.balance.domain.usecase.login.GetEmailUseCase

class AccountViewModelFactory(
    private val photoRepository: PhotoRepository,
    private val fetchProfileUseCase: FetchProfileUseCase,
    private val getEmailUseCase: GetEmailUseCase
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AccountViewModel(photoRepository, fetchProfileUseCase, getEmailUseCase) as T
    }
}