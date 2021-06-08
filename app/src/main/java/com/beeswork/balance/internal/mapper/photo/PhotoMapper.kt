package com.beeswork.balance.internal.mapper.photo

import com.beeswork.balance.data.database.entity.Photo
import com.beeswork.balance.ui.profile.photo.PhotoPicker

interface PhotoMapper {

//    fun toPhoto(photoDTO: PhotoDTO): Photo
    fun toPhotoPicker(photo: Photo): PhotoPicker
}