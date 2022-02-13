package com.beeswork.balance.ui.splashfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.data.database.repository.login.LoginRepository
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.data.database.repository.card.CardRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.internal.mapper.login.LoginMapper
import com.beeswork.balance.ui.common.BaseViewModel
import com.beeswork.balance.ui.loginactivity.LoginDomain
import kotlinx.coroutines.launch

class SplashViewModel(
    private val loginRepository: LoginRepository,
    private val loginMapper: LoginMapper,
    private val settingRepository: SettingRepository,
    private val cardRepository: CardRepository
): BaseViewModel() {

    private val _loginWithRefreshToken = MutableLiveData<Resource<LoginDomain>>()
    val loginWithRefreshToken: LiveData<Resource<LoginDomain>> get() = _loginWithRefreshToken

    fun loginWithRefreshToken() {
        viewModelScope.launch {
            val response = loginRepository.loginWithRefreshToken()
            var loginDomain: LoginDomain? = null
            response.data?.let { loginDTO ->
                if (loginDTO.profileExists && loginDTO.gender != null) {
                    cardRepository.prepopulateCardFilter(loginDTO.gender)
                }
                loginDomain = loginMapper.toLoginDomain(loginDTO)
            }
            _loginWithRefreshToken.postValue(
                response.map { loginDomain }
            )
        }
    }
}