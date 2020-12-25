package com.beeswork.balance.ui.profile

import android.net.Uri
import com.google.firebase.iid.GmsRpc

data class PhotoPicker(
    var key: String?,
    var status: Status,
    var uri: Uri?,
    var sequence: Int
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
        const val MAX_PHOTO_HEIGHT = 1920

        fun asEmpty(): PhotoPicker {
            return PhotoPicker(null, Status.EMPTY, null, Int.MAX_VALUE)
        }
    }
}