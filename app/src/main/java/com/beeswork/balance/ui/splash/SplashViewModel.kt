package com.beeswork.balance.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.data.database.repository.login.LoginRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.mapper.login.LoginMapper
import com.beeswork.balance.ui.common.BaseViewModel
import com.beeswork.balance.ui.loginactivity.LoginDomain
import kotlinx.coroutines.launch

class SplashViewModel(
    private val loginRepository: LoginRepository,
    private val loginMapper: LoginMapper
): BaseViewModel() {

    private val _loginWithRefreshToken = MutableLiveData<Resource<LoginDomain>>()
    val loginWithRefreshToken: LiveData<Resource<LoginDomain>> get() = _loginWithRefreshToken

    fun loginWithRefreshToken() {
        viewModelScope.launch(coroutineExceptionHandler) {
            val response = loginRepository.loginWithRefreshToken().let {
                it.mapData(it.data?.let { loginDTO -> loginMapper.toLoginDomain(loginDTO) })
            }
            _loginWithRefreshToken.postValue(response)
        }
    }

    fun login() {
        viewModelScope.launch {

        }
    }
}