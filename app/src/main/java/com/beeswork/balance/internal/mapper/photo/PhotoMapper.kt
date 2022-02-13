package com.beeswork.balance.internal.mapper.photo

import com.beeswork.balance.data.database.entity.photo.Photo
import com.beeswork.balance.data.network.response.photo.PhotoDTO
import com.beeswork.balance.ui.profilefragment.photo.PhotoPicker
import java.util.*

interface PhotoMapper {

    fun toPhoto(accountId: UUID, photoDTO: PhotoDTO): Photo
    fun toPhotoPicker(photo: Photo): PhotoPicker
}