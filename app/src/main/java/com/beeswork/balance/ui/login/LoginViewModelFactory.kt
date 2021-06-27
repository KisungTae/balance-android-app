package com.beeswork.balance.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.login.LoginRepository
import com.beeswork.balance.ui.splash.SplashViewModel

class LoginViewModelFactory(
    private val loginRepository: LoginRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginViewModel(loginRepository) as T
    }
}