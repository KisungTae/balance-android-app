package com.beeswork.balance.ui.setting.push

import androidx.lifecycle.*
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.data.network.response.Resource
import com.beeswork.balance.data.network.response.common.EmptyResponse
import com.beeswork.balance.internal.util.lazyDeferred
import kotlinx.coroutines.delay
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
            if (setting.matchPushSynced && setting.clickedPushSynced && setting.chatMessagePushSynced) return@launch

            if (!setting.matchPushSynced) _saveMatchPushLiveData.postValue(Resource.loading())
            if (!setting.clickedPushSynced) _saveClickedPushLiveData.postValue(Resource.loading())
            if (!setting.chatMessagePushSynced) _saveChatMessagePushLiveData.postValue(Resource.loading())

            val response = settingRepository.syncPushSettings(
                setting.matchPush,
                setting.clickedPush,
                setting.chatMessagePush
            )

            if (!setting.matchPushSynced) _saveMatchPushLiveData.postValue(response)
            if (!setting.clickedPushSynced) _saveClickedPushLiveData.postValue(response)
            if (!setting.chatMessagePushSynced) _saveChatMessagePushLiveData.postValue(response)

        }
    }

    fun saveMatchPush(matchPush: Boolean) {
        viewModelScope.launch {
            if (settingRepository.getMatchPush() != matchPush) {
                _saveMatchPushLiveData.postValue(Resource.loading())
                _saveMatchPushLiveData.postValue(settingRepository.saveMatchPush(matchPush))
            }
        }
    }

    fun saveClickedPush(clickedPush: Boolean) {
        viewModelScope.launch {
            if (settingRepository.getClickedPush() != clickedPush) {
                _saveClickedPushLiveData.postValue(Resource.loading())
                _saveClickedPushLiveData.postValue(settingRepository.saveClickedPush(clickedPush))
            }
        }
    }

    fun saveChatMessagePush(chatMessagePush: Boolean) {
        viewModelScope.launch {
            if (settingRepository.getChatMessagePush() != chatMessagePush) {
                _saveChatMessagePushLiveData.postValue(Resource.loading())
                _saveChatMessagePushLiveData.postValue(settingRepository.saveChatMessagePush(chatMessagePush))
            }
        }
    }

    fun test() {
        _syncPushSettingsLiveData.postValue(Resource.loading())
        viewModelScope.launch {
            delay(2000)
            _saveMatchPushLiveData.postValue(Resource.error("err"))
            delay(2000)
            _saveClickedPushLiveData.postValue(Resource.error("err"))
            delay(2000)
            _saveChatMessagePushLiveData.postValue(Resource.error("err"))
        }
    }

}