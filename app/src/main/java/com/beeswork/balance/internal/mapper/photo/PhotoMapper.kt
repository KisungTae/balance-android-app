package com.beeswork.balance.internal.mapper.photo

import com.beeswork.balance.data.database.entity.photo.Photo
import com.beeswork.balance.data.network.response.photo.PhotoDTO
import com.beeswork.balance.ui.photofragment.PhotoItemUIState

interface PhotoMapper {

    fun toPhoto(photoDTO: PhotoDTO): Photo
    fun toPhotoItemUIState(photo: Photo, balancePhotoBucketUrl: String?): PhotoItemUIState
}