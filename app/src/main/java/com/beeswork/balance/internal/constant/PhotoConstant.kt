package com.beeswork.balance.internal.constant

class PhotoConstant {
    companion object {
        const val MAX_NUM_OF_PHOTOS = 6
        const val MAX_PHOTO_SIZE = 1048576
        val PHOTO_MIME_TYPES = arrayOf("image/jpeg", "image/png", "image/jpg")
        const val PHOTO_INTENT_TYPE = "image/*"
        val PHOTO_EXTENSIONS = setOf("jpg", "jpeg", "gif", "png")
    }
}