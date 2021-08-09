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

    private val _validateLoginLiveData = MutableLiveData<Resource<EmptyResponse>>()
    val validateLoginLiveData: LiveData<Resource<EmptyResponse>> get() = _validateLoginLiveData

    fun validateLogin() {

    }

    fun login() {
        viewModelScope.launch {
            _validateLoginLiveData.postValue(loginRepository.login())
        }
    }
}