package com.beeswork.balance.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.login.LoginRepository
import com.beeswork.balance.internal.mapper.login.LoginMapper

class SplashViewModelFactory(
    private val loginRepository: LoginRepository,
    private val loginMapper: LoginMapper
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SplashViewModel(loginRepository, loginMapper) as T
    }
}