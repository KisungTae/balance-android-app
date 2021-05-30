package com.beeswork.balance.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogHeightOptionBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class HeightOptionDialog(
    private val heightOptionDialogListener: HeightOptionDialogListener,
    private val height: Int?
) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogHeightOptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogHeightOptionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        binding.npProfileHeightOption.maxValue = MAX_HEIGHT
        binding.npProfileHeightOption.minValue = MIN_HEIGHT
        binding.npProfileHeightOption.value = height ?: MIN_HEIGHT
        binding.npProfileHeightOption.wrapSelectorWheel = false
        binding.btnProfileHeightOptionClose.setOnClickListener { dismiss() }
        binding.btnProfileHeightOptionSelect.setOnClickListener {
            heightOptionDialogListener.onHeightChanged(binding.npProfileHeightOption.value.toString().toInt())
            dismiss()
        }
    }

    interface HeightOptionDialogListener {
        fun onHeightChanged(height: Int)
    }

    companion object {
        const val TAG = "heightOptionDialog"
        const val MAX_HEIGHT = 250
        const val MIN_HEIGHT = 130
    }
}