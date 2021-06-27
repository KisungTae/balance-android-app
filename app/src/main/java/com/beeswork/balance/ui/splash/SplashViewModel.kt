package com.beeswork.balance.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.data.database.repository.login.LoginRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import kotlinx.coroutines.launch

class SplashViewModel(
    private val loginRepository: LoginRepository
): ViewModel() {

    private val _loginLiveData = MutableLiveData<Resource<EmptyResponse>>()
    val loginLiveData: LiveData<Resource<EmptyResponse>> get() = _loginLiveData

    fun login() {
        viewModelScope.launch {
            _loginLiveData.postValue(loginRepository.login())
        }
    }
}