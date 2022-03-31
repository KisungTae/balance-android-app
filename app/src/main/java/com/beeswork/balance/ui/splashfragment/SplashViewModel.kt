package com.beeswork.balance.ui.splashfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.domain.uistate.login.LoginUIState
import com.beeswork.balance.domain.usecase.login.LoginWithRefreshTokenUseCase
import com.beeswork.balance.ui.common.BaseViewModel
import kotlinx.coroutines.launch

class SplashViewModel(
    private val loginWithRefreshTokenUseCase: LoginWithRefreshTokenUseCase
): BaseViewModel() {

    private val _loginWithRefreshTokenLiveData = MutableLiveData<LoginUIState>()
    val loginWithRefreshTokenLiveData: LiveData<LoginUIState> get() = _loginWithRefreshTokenLiveData

    fun loginWithRefreshToken() {
        viewModelScope.launch {
            val response = loginWithRefreshTokenUseCase.invoke()
            val loginUIState = if (response.isSuccess() && response.data != null) {
                LoginUIState.ofSuccess(response.data.profileExists)
            } else {
                LoginUIState.ofError(response.exception)
            }
            _loginWithRefreshTokenLiveData.postValue(loginUIState)
        }
    }
}