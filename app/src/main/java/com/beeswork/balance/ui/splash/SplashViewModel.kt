package com.beeswork.balance.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.data.database.repository.login.LoginRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.exception.AccountBlockedException
import com.beeswork.balance.internal.exception.AccountDeletedException
import com.beeswork.balance.internal.exception.AccountNotFoundException
import com.beeswork.balance.internal.exception.RefreshTokenExpiredException
import com.beeswork.balance.internal.mapper.login.LoginMapper
import com.beeswork.balance.ui.common.BaseViewModel
import com.beeswork.balance.ui.loginactivity.LoginDomain
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class SplashViewModel(
    private val loginRepository: LoginRepository,
    private val loginMapper: LoginMapper
): ViewModel() {

    private val _loginWithRefreshToken = MutableLiveData<Resource<LoginDomain>>()
    val loginWithRefreshToken: LiveData<Resource<LoginDomain>> get() = _loginWithRefreshToken

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _loginWithRefreshToken.postValue(Resource.error(""))
    }

    fun loginWithRefreshToken() {
        viewModelScope.launch(coroutineExceptionHandler) {
            val response = loginRepository.loginWithRefreshToken().let {
                it.mapData(it.data?.let { loginDTO -> loginMapper.toLoginDomain(loginDTO) })
            }
            _loginWithRefreshToken.postValue(response)
        }
    }
}