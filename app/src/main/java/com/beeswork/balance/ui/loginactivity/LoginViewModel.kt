package com.beeswork.balance.ui.loginactivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.data.database.repository.login.LoginRepository
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.data.database.repository.card.CardRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.internal.constant.LoginType
import com.beeswork.balance.internal.exception.InvalidSocialLoginException
import com.beeswork.balance.internal.mapper.login.LoginMapper
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginRepository: LoginRepository,
    private val settingRepository: SettingRepository,
    private val cardRepository: CardRepository,
    private val loginMapper: LoginMapper
) : ViewModel() {

    private val _loginLiveData = MutableLiveData<Resource<LoginDomain>>()
    val loginLiveData: LiveData<Resource<LoginDomain>> get() = _loginLiveData

//    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
//        _loginLiveData.postValue(Resource.error(throwable))
//    }

    fun socialLogin(loginId: String?, accessToken: String?, loginType: LoginType) {
        if (loginId.isNullOrBlank() || accessToken.isNullOrBlank()) {
            _loginLiveData.postValue(Resource.error(InvalidSocialLoginException()))
        } else {
            viewModelScope.launch {
                val response = loginRepository.socialLogin(loginId, accessToken, loginType)
                var loginDomain: LoginDomain? = null
                response.data?.let { loginDTO ->
                    if (loginDTO.profileExists && loginDTO.gender != null) {
                        cardRepository.prepopulateCardFilter(loginDTO.gender)
                    }
                    loginDomain = loginMapper.toLoginDomain(loginDTO)
                }
                _loginLiveData.postValue(
                    response.map { loginDomain }
                )
            }
        }
    }
}