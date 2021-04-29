package com.beeswork.balance.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogConfirmBinding
import com.beeswork.balance.databinding.DialogReportMenuBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ReportMenuDialog(
    private val reportMenuDialogClickListener: ReportMenuDialogClickListener
): BottomSheetDialogFragment() {

    private lateinit var binding: DialogReportMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogReportMenuBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
//        binding.btn.text = confirmButtonTitle
//        binding.btnConfirmDialogClose.setOnClickListener { dismiss() }
//        binding.btnConfirmDialogConfirm.setOnClickListener {
//            confirmDialogClickListener.onConfirm(requestCode, arguments)
//            dismiss()
//        }
    }

    companion object {
        const val TAG = "reportMenuDialog"
    }

    interface ReportMenuDialogClickListener {

    }
}