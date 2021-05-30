package com.beeswork.balance.ui.profile

data class PhotoPicker(
    var key: String?,
    var sequence: Int,
    var status: Status
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
            return PhotoPicker(null, Int.MAX_VALUE, Status.EMPTY)
        }
    }
}