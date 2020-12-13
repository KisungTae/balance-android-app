package com.beeswork.balance.ui.profile

import android.net.Uri
import com.google.firebase.iid.GmsRpc

data class PhotoPicker(
    var key: String?,
    var status: Status,
    var uri: Uri?
) {

    enum class Status {
        EMPTY,
        LOADING,
        ERROR,
        UPLOADED
    }

    companion object {

        const val MAXIMUM_NUM_OF_PHOTOS = 6
        const val MAX_PHOTO_WIDTH = 1200
        const val MAX_PHOTO_HEIGHT = 1920

        fun empty(): PhotoPicker {
            return PhotoPicker(null, PhotoPicker.Status.EMPTY, null)
        }
    }
}