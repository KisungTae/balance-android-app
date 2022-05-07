package com.beeswork.balance.ui.mainactivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.main.MainRepository
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.domain.usecase.location.SaveLocationUseCase
import com.beeswork.balance.domain.usecase.main.ConnectToStompUseCase
import com.beeswork.balance.domain.usecase.main.DisconnectStompUseCase
import kotlinx.coroutines.CoroutineDispatcher

class MainViewModelFactory(
    private val mainRepository: MainRepository,
    private val connectToStompUseCase: ConnectToStompUseCase,
    private val disconnectStompUseCase: DisconnectStompUseCase,
    private val defaultDispatcher: CoroutineDispatcher
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(mainRepository, connectToStompUseCase, disconnectStompUseCase, defaultDispatcher) as T
    }


}