package com.beeswork.balance.ui.setting.push

import androidx.lifecycle.*
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.internal.mapper.setting.PushSettingMapper
import com.beeswork.balance.ui.common.BaseViewModel
import kotlinx.coroutines.launch

class PushSettingViewModel(
    private val settingRepository: SettingRepository,
    private val pushSettingMapper: PushSettingMapper
) : BaseViewModel() {

    private val _fetchPushSettingLiveData = MutableLiveData<Resource<PushSettingDomain>>()
    val fetchPushSettingLiveData: LiveData<Resource<PushSettingDomain>> get() = _fetchPushSettingLiveData

    private val _savePushSettingLiveData = MutableLiveData<Resource<PushSettingDomain>>()
    val savePushSettingLiveData: LiveData<Resource<PushSettingDomain>> get() = _savePushSettingLiveData

    fun savePushSetting(matchPush: Boolean, clickedPush: Boolean, chatMessagePush: Boolean, emailPush: Boolean) {
        viewModelScope.launch {
            _savePushSettingLiveData.postValue(Resource.loading())
            val response = settingRepository.savePushSetting(matchPush, clickedPush, chatMessagePush, emailPush).map {
                it?.let { _pushSetting -> pushSettingMapper.toPushSettingDomain(_pushSetting) }
            }
            _savePushSettingLiveData.postValue(response)
        }
    }

    fun fetchPushSetting() {
        viewModelScope.launch {
            val pushSetting = settingRepository.getPushSetting()
            val pushSettingDomain = pushSetting?.let { _pushSetting ->
                pushSettingMapper.toPushSettingDomain(_pushSetting)
            }
            val isPushSettingSynced = pushSetting?.synced == true
            if (isPushSettingSynced)
                _fetchPushSettingLiveData.postValue(Resource.success(pushSettingDomain))
            else {
                _fetchPushSettingLiveData.postValue(Resource.loading(pushSettingDomain))
                val response = settingRepository.fetchPushSetting().map {
                    it?.let { _pushSetting -> pushSettingMapper.toPushSettingDomain(_pushSetting) }
                }
                _fetchPushSettingLiveData.postValue(response)
            }
        }
    }
}