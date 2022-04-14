package com.beeswork.balance.ui.registeractivity.photo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.photo.PhotoRepository
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.domain.usecase.photo.FetchPhotosUseCase
import com.beeswork.balance.ui.registeractivity.name.NameViewModel

class PhotoViewModelFactory (
    private val fetchPhotosUseCase: FetchPhotosUseCase
): ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PhotoViewModel(fetchPhotosUseCase) as T
    }
}