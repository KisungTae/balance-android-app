package com.beeswork.balance.ui.registeractivity.photo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beeswork.balance.data.database.repository.photo.PhotoRepository
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import kotlinx.coroutines.launch

open class PhotoViewModel (
    private val photoRepository: PhotoRepository
): ViewModel() {


    fun orderPhotos(photoSequences: Map<String, Int>) {
    }
}