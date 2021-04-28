package com.beeswork.balance.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogConfirmBinding
import com.beeswork.balance.databinding.DialogErrorBinding
import com.beeswork.balance.internal.constant.RequestCode
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ConfirmDialog (
    private val confirmButtonTitle: String,
    private val requestCode: Int,
    private val confirmDialogClickListener: ConfirmDialogClickListener
): BottomSheetDialogFragment() {

    private lateinit var binding: DialogConfirmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialog)
//        setStyle(STYLE_NO_FRAME, R.style.CustomBottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogConfirmBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        binding.btnConfirmDialogConfirm.text = confirmButtonTitle
        binding.btnConfirmDialogClose.setOnClickListener { dismiss() }
        binding.btnConfirmDialogConfirm.setOnClickListener {
            confirmDialogClickListener.onConfirm(requestCode, arguments)
            dismiss()
        }
    }

    companion object {
        const val TAG = "confirmDialog"
    }


    interface ConfirmDialogClickListener {
        fun onConfirm(requestCode: Int, argument: Bundle?)
    }




}