package com.beeswork.balance.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beeswork.balance.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_photo_upload_option.*

class PhotoUploadOptionDialog(
    private val photoUploadOptionListener: PhotoUploadOptionListener
): BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_photo_upload_option, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnUploadFromGallery.setOnClickListener {
            dismiss()
            photoUploadOptionListener.onClickUploadFromGallery()
        }
        btnUploadFromCapture.setOnClickListener {
            dismiss()
            photoUploadOptionListener.onClickUploadFromCapture()
        }
    }

    companion object {
        const val TAG = "photoUploadOptionDialog"
    }

    interface PhotoUploadOptionListener {
        fun onClickUploadFromGallery()
        fun onClickUploadFromCapture()
    }
}