package com.beeswork.balance.internal.mapper.photo

import com.beeswork.balance.data.database.entity.Photo
import com.beeswork.balance.data.network.response.photo.PhotoDTO
import com.beeswork.balance.ui.profile.photo.PhotoPicker

class PhotoMapperImpl : PhotoMapper {

    override fun toPhotoPicker(photo: Photo): PhotoPicker {
        return PhotoPicker(photo.key, photo.status, photo.uri, photo.sequence)
    }

}