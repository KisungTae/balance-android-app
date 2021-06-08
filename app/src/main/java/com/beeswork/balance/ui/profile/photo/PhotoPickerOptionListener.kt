package com.beeswork.balance.ui.profile.photo

import android.net.Uri

interface PhotoPickerOptionListener {
    fun uploadPhoto(photoUri: Uri?, photoKey: String?)
    fun downloadPhoto(photoKey: String?)
    fun deletePhoto(photoKey: String?)
    fun uploadPhotoFromGallery()
    fun uploadPhotoFromCapture()
}