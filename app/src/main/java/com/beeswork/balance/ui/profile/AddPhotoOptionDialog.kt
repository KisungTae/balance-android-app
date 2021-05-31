package com.beeswork.balance.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogAddPhotoOptionBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddPhotoOptionDialog(
    private val addPhotoOptionListener: AddPhotoOptionListener
) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogAddPhotoOptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogAddPhotoOptionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnPhotoAddOptionClose.setOnClickListener { dismiss() }
        binding.btnUploadPhotoFromCapture.setOnClickListener {
            dismiss()
            addPhotoOptionListener.onUploadPhotoFromCapture()
        }
        binding.btnUploadPhotoFromGallery.setOnClickListener {
            dismiss()
            addPhotoOptionListener.onUploadPhotoFromGallery()
        }
    }

    companion object {
        const val TAG = "photoAddOptionDialog"
    }

    interface AddPhotoOptionListener {
        fun onUploadPhotoFromGallery()
        fun onUploadPhotoFromCapture()
    }
}