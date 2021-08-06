package com.beeswork.balance.ui.mainactivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.main.MainRepository
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.data.network.service.fcm.FCMService

class MainViewModelFactory(
    private val mainRepository: MainRepository,
    private val settingRepository: SettingRepository,
    private val fcmService: FCMService
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(mainRepository, settingRepository, fcmService) as T
    }


}