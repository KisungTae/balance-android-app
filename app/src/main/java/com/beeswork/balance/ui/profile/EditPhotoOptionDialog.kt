package com.beeswork.balance.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogEditPhotoOptionBinding

import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EditPhotoOptionDialog: BottomSheetDialogFragment() {

    private lateinit var binding: DialogEditPhotoOptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogEditPhotoOptionBinding.inflate(inflater)
        return binding.root
    }

    interface EditPhotoOptionListener {

    }

    companion object {
        const val TAG = "photoEditOptionDialog"
    }
}