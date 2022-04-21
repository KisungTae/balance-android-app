package com.beeswork.balance.ui.photofragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogUploadPhotoOptionBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class UploadPhotoOptionDialog(
    private val photoPickerOptionListener: PhotoPickerOptionListener
) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogUploadPhotoOptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogUploadPhotoOptionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnPhotoAddOptionClose.setOnClickListener { dismiss() }
        binding.btnUploadPhotoFromCapture.setOnClickListener {
            dismiss()
            photoPickerOptionListener.uploadPhotoFromCapture()
        }
        binding.btnUploadPhotoFromGallery.setOnClickListener {
            dismiss()
            photoPickerOptionListener.uploadPhotoFromGallery()
        }
    }

    companion object {
        const val TAG = "photoAddOptionDialog"
    }
}