package com.beeswork.balance.ui.loginactivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.login.LoginRepository
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.data.database.repository.swipe.SwipeRepository
import com.beeswork.balance.internal.mapper.login.LoginMapper

class LoginViewModelFactory(
    private val loginRepository: LoginRepository,
    private val settingRepository: SettingRepository,
    private val swipeRepository: SwipeRepository,
    private val loginMapper: LoginMapper
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginViewModel(loginRepository, settingRepository, swipeRepository, loginMapper) as T
    }
}