package com.beeswork.balance.ui.registeractivity.photo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.photo.PhotoRepository
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.domain.usecase.photo.*
import com.beeswork.balance.ui.registeractivity.name.NameViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class PhotoViewModelFactory(
    private val uploadPhotoUseCase: UploadPhotoUseCase,
    private val deletePhotoUseCase: DeletePhotoUseCase,
    private val orderPhotosUseCase: OrderPhotosUseCase,
    private val syncPhotosUseCase: SyncPhotosUseCase,
    private val photoRepository: PhotoRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PhotoViewModel(
            uploadPhotoUseCase,
            deletePhotoUseCase,
            orderPhotosUseCase,
            syncPhotosUseCase,
            photoRepository,
            defaultDispatcher
        ) as T
    }
}