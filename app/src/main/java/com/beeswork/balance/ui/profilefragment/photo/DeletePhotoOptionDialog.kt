package com.beeswork.balance.ui.profilefragment.photo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogDeletePhotoOptionBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DeletePhotoOptionDialog(
    private val photoKey: String?,
    private val photoPickerOptionListener: PhotoPickerOptionListener
) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogDeletePhotoOptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogDeletePhotoOptionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnDeletePhotoClose.setOnClickListener { dismiss() }
        binding.btnDeletePhoto.setOnClickListener {
            dismiss()
            photoPickerOptionListener.deletePhoto(photoKey)
        }
    }

    companion object {
        const val TAG = "deletePhotoOptionDialog"
    }
}