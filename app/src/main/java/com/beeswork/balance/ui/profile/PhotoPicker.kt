package com.beeswork.balance.ui.profile

import android.net.Uri
import com.beeswork.balance.internal.constant.PhotoStatus
import java.util.*

data class PhotoPicker(
    var key: UUID?,
    var status: PhotoStatus,
    var uriPath: Uri?,
    var sequence: Int,
) {

    enum class Status {
        EMPTY,
        LOADING,
        DOWNLOADING,
        UPLOADING,
        UPLOAD_ERROR,
        DOWNLOAD_ERROR,
        OCCUPIED
    }

    companion object {
        const val MAX_PHOTO_WIDTH = 1200
        const val MAX_PHOTO_HEIGHT = 1560

        fun asEmpty(): PhotoPicker {
            return PhotoPicker(null, PhotoStatus.EMPTY, null, Int.MAX_VALUE)
        }
    }
}