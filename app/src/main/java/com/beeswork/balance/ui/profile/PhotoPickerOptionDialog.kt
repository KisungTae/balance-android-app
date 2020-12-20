package com.beeswork.balance.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beeswork.balance.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_photo_picker_option.*

class PhotoPickerOptionDialog(
    private val photoPickerOptionListener: PhotoPickerOptionListener,
    private val photoKey: String?,
    private val photoPickerStatus: PhotoPicker.Status
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_photo_picker_option, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (photoPickerStatus) {
            PhotoPicker.Status.EMPTY -> {
                btnUploadPhotoFromCapture.visibility = View.VISIBLE
                btnUploadPhotoFromCapture.setOnClickListener {
                    dismiss()
                    photoPickerOptionListener.onUploadPhotoFromCapture()
                }

                btnUploadPhotoFromGallery.visibility = View.VISIBLE
                btnUploadPhotoFromGallery.setOnClickListener {
                    dismiss()
                    photoPickerOptionListener.onUploadPhotoFromGallery()
                }
            }
            PhotoPicker.Status.UPLOAD_ERROR -> {
                btnPhotoErrorDelete.visibility = View.VISIBLE
                btnPhotoErrorUpload.visibility = View.VISIBLE
                photoKey?.let { key ->
                    btnPhotoErrorUpload.setOnClickListener {
                        dismiss()
                        photoPickerOptionListener.onReuploadPhoto(key)
                    }
                    btnPhotoErrorDelete.setOnClickListener {
                        dismiss()
                        photoPickerOptionListener.onDeletePhoto(key, photoPickerStatus)
                    }
                }
            }
            PhotoPicker.Status.DOWNLOAD_ERROR -> {
                btnPhotoErrorDelete.visibility = View.VISIBLE
                btnPhotoErrorDownload.visibility = View.VISIBLE
                photoKey?.let { key ->
                    btnPhotoErrorDownload.setOnClickListener {
                        dismiss()
                        photoPickerOptionListener.onRedownloadPhoto(key)
                    }
                    btnPhotoErrorDelete.setOnClickListener {
                        dismiss()
                        photoPickerOptionListener.onDeletePhoto(key, photoPickerStatus)
                    }
                }
            }
            else -> dismiss()
        }
    }

    interface PhotoPickerOptionListener {
        fun onDeletePhoto(photoKey: String, photoPickerStatus: PhotoPicker.Status)
        fun onReuploadPhoto(photoKey: String)
        fun onRedownloadPhoto(photoKey: String)
        fun onUploadPhotoFromGallery()
        fun onUploadPhotoFromCapture()
    }

    companion object {
        const val TAG = "photoPickerOptionDialog"
    }
}