package com.beeswork.balance.ui.profilefragment.photo

import android.net.Uri
import com.beeswork.balance.internal.constant.PhotoStatus

data class PhotoItemUIState(
    var key: String?,
    var status: PhotoStatus,
    val deleting: Boolean,
    var uri: Uri?,
    var sequence: Int,
    val url: String?
) {

    companion object {
        const val MAX_PHOTO_WIDTH = 1200
        const val MAX_PHOTO_HEIGHT = 1560

        fun asEmpty(): PhotoItemUIState {
            return PhotoItemUIState(null, PhotoStatus.EMPTY, false, null, Int.MAX_VALUE, null)
        }

        fun asLoading(): PhotoItemUIState {
            return PhotoItemUIState(null, PhotoStatus.LOADING, false, null, Int.MAX_VALUE, null)
        }
    }
}