package com.beeswork.balance.internal.mapper.photo

import com.beeswork.balance.data.database.entity.Photo
import com.beeswork.balance.data.network.response.photo.PhotoDTO
import com.beeswork.balance.ui.profile.PhotoPicker

class PhotoMapperImpl : PhotoMapper {

    override fun toPhoto(photoDTO: PhotoDTO): Photo {
        return Photo(photoDTO.key, photoDTO.sequence, false)
    }

    override fun toPhotoPicker(photo: Photo): PhotoPicker {
        return PhotoPicker(photo.key, photo.sequence, PhotoPicker.Status.DOWNLOADING)
    }

}