package com.beeswork.balance.ui.loginactivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.login.LoginRepository
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.data.database.repository.card.CardRepository
import com.beeswork.balance.domain.usecase.login.SocialLoginUseCase
import com.beeswork.balance.internal.mapper.login.LoginMapper

class LoginViewModelFactory(
    private val socialLoginUseCase: SocialLoginUseCase
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginViewModel(socialLoginUseCase) as T
    }
}