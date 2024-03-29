package com.beeswork.balance.internal.mapper.photo

import com.beeswork.balance.data.database.entity.photo.Photo
import com.beeswork.balance.data.network.response.photo.PhotoDTO
import com.beeswork.balance.internal.constant.EndPoint
import com.beeswork.balance.internal.constant.PhotoStatus
import com.beeswork.balance.ui.photofragment.PhotoItemUIState

class PhotoMapperImpl : PhotoMapper {

    override fun toPhoto(photoDTO: PhotoDTO): Photo {
        return Photo(
            photoDTO.key,
            photoDTO.accountId,
            PhotoStatus.DOWNLOADING,
            deleting = false,
            null,
            photoDTO.sequence,
            photoDTO.sequence,
            uploaded = true,
            saved = true
        )
    }

    override fun toPhotoItemUIState(photo: Photo): PhotoItemUIState {
        return PhotoItemUIState(
            photo.key,
            photo.status,
            photo.deleting,
            photo.uri,
            EndPoint.ofPhoto(photo.accountId, photo.key)
        )
    }

}