package com.beeswork.balance.ui.splashfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.login.LoginRepository
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.data.database.repository.card.CardRepository
import com.beeswork.balance.domain.usecase.login.LoginWithRefreshTokenUseCase
import com.beeswork.balance.internal.mapper.login.LoginMapper

class SplashViewModelFactory(
    private val loginWithRefreshTokenUseCase: LoginWithRefreshTokenUseCase
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SplashViewModel(loginWithRefreshTokenUseCase) as T
    }
}