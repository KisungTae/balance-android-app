package com.beeswork.balance.ui.profile.photo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogDownloadPhotoErrorOptionBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DownloadPhotoErrorOptionDialog(
    private val photoKey: String?,
    private val photoPickerOptionListener: PhotoPickerOptionListener
) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogDownloadPhotoErrorOptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogDownloadPhotoErrorOptionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnDownloadPhotoErrorClose.setOnClickListener { dismiss() }
        binding.btnDownloadPhotoErrorDelete.setOnClickListener {
            dismiss()
            photoPickerOptionListener.deletePhoto(photoKey)
        }
        binding.btnDownloadPhotoErrorRedownload.setOnClickListener {
            dismiss()
            photoPickerOptionListener.redownloadPhoto(photoKey)
        }
    }

    companion object {
        const val TAG = "downloadPhotoErrorOptionDialog"
    }
}