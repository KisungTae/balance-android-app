package com.beeswork.balance.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.login.LoginRepository

class SplashViewModelFactory(
    private val loginRepository: LoginRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SplashViewModel(loginRepository) as T
    }
}