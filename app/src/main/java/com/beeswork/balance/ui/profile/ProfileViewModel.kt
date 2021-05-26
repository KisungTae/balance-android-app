package com.beeswork.balance.ui.profile

import androidx.lifecycle.ViewModel
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.internal.mapper.profile.ProfileMapper

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val profileMapper: ProfileMapper
): ViewModel() {
}