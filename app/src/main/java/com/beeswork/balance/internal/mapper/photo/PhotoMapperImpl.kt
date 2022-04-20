package com.beeswork.balance.internal.mapper.photo

import com.beeswork.balance.data.database.entity.photo.Photo
import com.beeswork.balance.data.network.response.photo.PhotoDTO
import com.beeswork.balance.internal.constant.EndPoint
import com.beeswork.balance.internal.constant.PhotoStatus
import com.beeswork.balance.ui.profilefragment.photo.PhotoItemUIState

class PhotoMapperImpl : PhotoMapper {

    override fun toPhoto(photoDTO: PhotoDTO): Photo {
        return Photo(
            photoDTO.key,
            photoDTO.accountId,
            PhotoStatus.DOWNLOADING,
            false,
            null,
            photoDTO.sequence,
            photoDTO.sequence,
            uploaded = true,
            saved = true
        )
    }

    override fun toPhotoPicker(photo: Photo): PhotoItemUIState {
        return PhotoItemUIState(
            photo.key,
            photo.status,
            photo.deleting,
            photo.uri,
            photo.sequence,
            EndPoint.ofPhoto(photo.accountId, photo.key)
        )
    }

}