package com.beeswork.balance.ui.settingfragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.chat.ChatRepository
import com.beeswork.balance.data.database.repository.swipe.SwipeRepository
import com.beeswork.balance.data.database.repository.login.LoginRepository
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.data.database.repository.photo.PhotoRepository
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.data.database.repository.card.CardRepository
import com.beeswork.balance.internal.mapper.location.LocationMapper

class SettingViewModelFactory(
    private val settingRepository: SettingRepository,
    private val chatRepository: ChatRepository,
    private val swipeRepository: SwipeRepository,
    private val matchRepository: MatchRepository,
    private val photoRepository: PhotoRepository,
    private val cardRepository: CardRepository,
    private val profileRepository: ProfileRepository,
    private val loginRepository: LoginRepository,
    private val locationMapper: LocationMapper
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SettingViewModel(
            settingRepository,
            chatRepository,
            swipeRepository,
            matchRepository,
            photoRepository,
            cardRepository,
            profileRepository,
            loginRepository,
            locationMapper
        ) as T
    }
}