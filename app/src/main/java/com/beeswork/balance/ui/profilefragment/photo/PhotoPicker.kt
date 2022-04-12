package com.beeswork.balance.ui.profilefragment.photo

import android.net.Uri
import com.beeswork.balance.internal.constant.PhotoStatus

data class PhotoPicker(
    var key: String?,
    var status: PhotoStatus,
    var uri: Uri?,
    var sequence: Int,
    val url: String?
) {

    companion object {
        const val MAX_PHOTO_WIDTH = 1200
        const val MAX_PHOTO_HEIGHT = 1560

        fun asEmpty(): PhotoPicker {
            return PhotoPicker(null, PhotoStatus.EMPTY, null, Int.MAX_VALUE, null)
        }
    }
}