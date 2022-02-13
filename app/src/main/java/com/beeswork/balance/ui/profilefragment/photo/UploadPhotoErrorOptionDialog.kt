package com.beeswork.balance.ui.profilefragment.photo

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogUploadPhotoErrorOptionBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class UploadPhotoErrorOptionDialog(
    private val photoUri: Uri?,
    private val photoKey: String?,
    private val photoPickerOptionListener: PhotoPickerOptionListener
) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogUploadPhotoErrorOptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogUploadPhotoErrorOptionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnUploadPhotoErrorClose.setOnClickListener { dismiss() }
        binding.btnUploadPhotoErrorDelete.setOnClickListener {
            dismiss()
            photoPickerOptionListener.deletePhoto(photoKey)
        }
        binding.btnUploadPhotoErrorReupload.setOnClickListener {
            dismiss()
            photoPickerOptionListener.reuploadPhoto(photoUri, photoKey)
        }
    }

    companion object {
        const val TAG = "uploadPhotoErrorOptionDialog"
    }
}