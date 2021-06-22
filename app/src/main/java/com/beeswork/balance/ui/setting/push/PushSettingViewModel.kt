package com.beeswork.balance.ui.setting.push

import androidx.lifecycle.*
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.util.lazyDeferred
import kotlinx.coroutines.launch

class PushSettingViewModel(
    private val settingRepository: SettingRepository
) : ViewModel() {

    val pushSettings by lazyDeferred { settingRepository.getPushSettingsFlow().asLiveData() }

    private val _saveMatchPushLiveData = MutableLiveData<Resource<EmptyResponse>>()
    val saveMatchPushLiveData: LiveData<Resource<EmptyResponse>> get() = _saveMatchPushLiveData

    private val _saveClickedPushLiveData = MutableLiveData<Resource<EmptyResponse>>()
    val saveClickedPushLiveData: LiveData<Resource<EmptyResponse>> get() = _saveClickedPushLiveData

    private val _saveChatMessagePushLiveData = MutableLiveData<Resource<EmptyResponse>>()
    val saveChatMessagePushLiveData: LiveData<Resource<EmptyResponse>> get() = _saveChatMessagePushLiveData

    fun syncPushSettings() {
        viewModelScope.launch {
            val setting = settingRepository.getSetting()
            if (!setting.matchPushSynced) saveMatchPush(setting.matchPush)
            if (!setting.clickedPushSynced) saveClickedPush(setting.clickedPush)
            if (!setting.chatMessagePushSynced) saveChatMessagePush(setting.chatMessagePush)
        }
    }

    fun saveMatchPush(matchPush: Boolean) {
        viewModelScope.launch {
            _saveMatchPushLiveData.postValue(Resource.loading())
            _saveMatchPushLiveData.postValue(settingRepository.saveMatchPush(matchPush))
        }
    }

    fun saveClickedPush(clickedPush: Boolean) {
        viewModelScope.launch {
            _saveClickedPushLiveData.postValue(Resource.loading())
            _saveClickedPushLiveData.postValue(settingRepository.saveClickedPush(clickedPush))
        }
    }

    fun saveChatMessagePush(chatMessagePush: Boolean) {
        viewModelScope.launch {
            _saveChatMessagePushLiveData.postValue(Resource.loading())
            _saveChatMessagePushLiveData.postValue(settingRepository.saveChatMessagePush(chatMessagePush))
        }
    }

}