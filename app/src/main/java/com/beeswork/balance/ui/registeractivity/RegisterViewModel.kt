package com.beeswork.balance.ui.registeractivity

import androidx.lifecycle.ViewModel
import com.beeswork.balance.data.database.repository.photo.PhotoRepository
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.database.repository.swipe.SwipeRepository

class RegisterViewModel(
    private val profileRepository: ProfileRepository,
    private val photoRepository: PhotoRepository
): ViewModel() {

}