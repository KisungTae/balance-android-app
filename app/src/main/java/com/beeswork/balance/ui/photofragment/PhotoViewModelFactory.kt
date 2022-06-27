package com.beeswork.balance.ui.photofragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.data.database.repository.photo.PhotoRepository
import com.beeswork.balance.domain.usecase.photo.*
import com.beeswork.balance.internal.mapper.photo.PhotoMapper
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class PhotoViewModelFactory(
    private val uploadPhotoUseCase: UploadPhotoUseCase,
    private val deletePhotoUseCase: DeletePhotoUseCase,
    private val orderPhotosUseCase: OrderPhotosUseCase,
    private val syncPhotosUseCase: SyncPhotosUseCase,
    private val updatePhotoStatusUseCase: UpdatePhotoStatusUseCase,
    private val photoRepository: PhotoRepository,
    private val photoMapper: PhotoMapper,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PhotoViewModel(
            uploadPhotoUseCase,
            deletePhotoUseCase,
            orderPhotosUseCase,
            syncPhotosUseCase,
            updatePhotoStatusUseCase,
            photoRepository,
            photoMapper,
            defaultDispatcher
        ) as T
    }
}