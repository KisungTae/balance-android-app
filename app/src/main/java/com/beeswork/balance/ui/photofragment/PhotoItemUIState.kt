package com.beeswork.balance.ui.photofragment

import android.net.Uri
import com.beeswork.balance.internal.constant.PhotoStatus

data class PhotoItemUIState(
    var key: String?,
    var status: PhotoStatus,
    val deleting: Boolean,
    var uri: Uri?,
    val url: String?
) {

    companion object {
        const val MAX_PHOTO_WIDTH = 1200
        const val MAX_PHOTO_HEIGHT = 1560

        fun asEmpty(): PhotoItemUIState {
            return PhotoItemUIState(null, PhotoStatus.EMPTY, false, null, null)
        }

        fun asLoading(): PhotoItemUIState {
            return PhotoItemUIState(null, PhotoStatus.LOADING, false, null, null)
        }
    }
}