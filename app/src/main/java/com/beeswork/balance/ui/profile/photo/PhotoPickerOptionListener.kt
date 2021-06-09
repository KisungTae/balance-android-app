package com.beeswork.balance.ui.profile.photo

import android.net.Uri

interface PhotoPickerOptionListener {
    fun reuploadPhoto(photoUri: Uri?, photoKey: String?)
    fun redownloadPhoto(photoKey: String?)
    fun deletePhoto(photoKey: String?)
    fun uploadPhotoFromGallery()
    fun uploadPhotoFromCapture()
}