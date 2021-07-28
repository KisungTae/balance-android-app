package com.beeswork.balance.ui.setting.email

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.login.LoginRepository
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.internal.mapper.setting.LoginMapper

class EmailSettingViewModelFactory(
    private val loginRepository: LoginRepository,
    private val loginMapper: LoginMapper
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EmailSettingViewModel(loginRepository, loginMapper) as T
    }
}