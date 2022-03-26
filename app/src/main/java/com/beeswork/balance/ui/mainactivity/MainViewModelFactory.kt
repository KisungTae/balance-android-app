package com.beeswork.balance.ui.mainactivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.main.MainRepository
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import kotlinx.coroutines.CoroutineDispatcher

class MainViewModelFactory(
    private val mainRepository: MainRepository,
    private val settingRepository: SettingRepository,
    private val defaultDispatcher: CoroutineDispatcher
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(mainRepository, settingRepository, defaultDispatcher) as T
    }


}