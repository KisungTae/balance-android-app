package com.beeswork.balance.ui.setting.push

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import kotlinx.coroutines.launch

class PushSettingViewModel(
    private val settingRepository: SettingRepository
) : ViewModel() {

    fun updateMatchPush(matchPush: Boolean) {
        viewModelScope.launch { settingRepository.updateMatchPush(matchPush) }
    }

    fun updateClickedPush(clickedPush: Boolean) {
        viewModelScope.launch { settingRepository.updateClickedPush(clickedPush) }
    }

    fun updateChatMessagePush(chatMessagePush: Boolean) {
        viewModelScope.launch { settingRepository.updateChatMessagePush(chatMessagePush) }
    }
}