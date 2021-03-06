package com.beeswork.balance.ui.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogPhotoPickerOptionBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PhotoPickerOptionDialog(
    private val photoPickerOptionListener: PhotoPickerOptionListener,
    private val photoKey: String?,
    private val photoPickerStatus: PhotoPicker.Status,
    private val photoUri: Uri?
) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogPhotoPickerOptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogPhotoPickerOptionBinding.inflate(layoutInflater)
    }

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
                binding.btnUploadPhotoFromCapture.visibility = View.VISIBLE
                binding.btnUploadPhotoFromCapture.setOnClickListener {
                    dismiss()
                    photoPickerOptionListener.onUploadPhotoFromCapture()
                }

                binding.btnUploadPhotoFromGallery.visibility = View.VISIBLE
                binding.btnUploadPhotoFromGallery.setOnClickListener {
                    dismiss()
                    photoPickerOptionListener.onUploadPhotoFromGallery()
                }
            }
            PhotoPicker.Status.UPLOAD_ERROR -> {
                binding.btnPhotoErrorDelete.visibility = View.VISIBLE
                binding.btnPhotoErrorUpload.visibility = View.VISIBLE
                binding.btnPhotoErrorUpload.setOnClickListener {
                    dismiss()
                    photoKey?.let { photoPickerOptionListener.onReuploadPhoto(it, photoUri) }
                }
                setDeleteBtnListener()
            }
            PhotoPicker.Status.DOWNLOAD_ERROR -> {
                binding.btnPhotoErrorDelete.visibility = View.VISIBLE
                binding.btnPhotoErrorDownload.visibility = View.VISIBLE
                binding.btnPhotoErrorDownload.setOnClickListener {
                    dismiss()
                    photoKey?.let { photoPickerOptionListener.onRedownloadPhoto(it) }
                }
                setDeleteBtnListener()
            }
            PhotoPicker.Status.OCCUPIED -> {
                binding.btnPhotoErrorDelete.visibility = View.VISIBLE
                setDeleteBtnListener()
            }
            else -> dismiss()
        }
    }

    private fun setDeleteBtnListener() {
        binding.btnPhotoErrorDelete.setOnClickListener {
            dismiss()
            photoKey?.let { photoPickerOptionListener.onDeletePhoto(it, photoPickerStatus) }
        }
    }

    interface PhotoPickerOptionListener {
        fun onDeletePhoto(photoKey: String, photoPickerStatus: PhotoPicker.Status)
        fun onReuploadPhoto(photoKey: String, photoUri: Uri?)
        fun onRedownloadPhoto(photoKey: String)
        fun onUploadPhotoFromGallery()
        fun onUploadPhotoFromCapture()
    }

    companion object {
        const val TAG = "photoPickerOptionDialog"
    }
}