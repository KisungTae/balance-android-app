package com.beeswork.balance.ui.login

import androidx.lifecycle.ViewModel
import com.beeswork.balance.data.database.repository.login.LoginRepository

class LoginViewModel(
    private val loginRepository: LoginRepository
): ViewModel() {
}