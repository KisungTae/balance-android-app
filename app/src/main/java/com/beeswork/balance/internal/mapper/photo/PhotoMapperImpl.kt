package com.beeswork.balance.internal.mapper.photo

import com.beeswork.balance.data.database.entity.Photo
import com.beeswork.balance.data.network.response.photo.PhotoDTO
import com.beeswork.balance.internal.constant.PhotoStatus
import com.beeswork.balance.ui.profile.photo.PhotoPicker
import java.util.*

class PhotoMapperImpl : PhotoMapper {

    override fun toPhoto(accountId: UUID, photoDTO: PhotoDTO): Photo {
        return Photo(
            photoDTO.key,
            accountId,
            PhotoStatus.DOWNLOADING,
            null,
            photoDTO.sequence,
            photoDTO.sequence,
            uploaded = true,
            saved = true
        )
    }

    override fun toPhotoPicker(photo: Photo): PhotoPicker {
        return PhotoPicker(photo.key, photo.status, photo.uri, photo.sequence)
    }

}