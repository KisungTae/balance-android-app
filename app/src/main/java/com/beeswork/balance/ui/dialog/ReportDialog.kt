package com.beeswork.balance.ui.dialog


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogReportBinding
import com.beeswork.balance.internal.constant.ReportReason
import com.beeswork.balance.internal.util.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class ReportDialog(
    private val reportDialogClickListener: ReportDialogClickListener
) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogReportBinding
    private var reportReason: ReportReason? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogReportBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setOnShowListener {
            val dialog = it as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(R.id.design_bottom_sheet)
            bottomSheet?.let { sheet ->
                dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                sheet.parent.parent.requestLayout()
            }
        }
        bindUI()
    }

    private fun bindUI() {
        binding.btnReportDialogClose.setOnClickListener { dismiss() }
        binding.btnReportDialogBack.setOnClickListener { showReportOption() }
        binding.btnReportDialogMessage.setOnClickListener { showReportDetail(ReportReason.MESSAGE, it.getText()) }
        binding.btnReportDialogBehaviour.setOnClickListener { showReportDetail(ReportReason.BEHAVIOUR, it.getText()) }
        binding.btnReportDialogSpam.setOnClickListener { showReportDetail(ReportReason.SPAM, it.getText()) }
        binding.btnReportDialogPhoto.setOnClickListener { showReportDetail(ReportReason.PHOTO, it.getText()) }
        binding.btnReportDialogOther.setOnClickListener { showReportDetail(ReportReason.OTHER, it.getText()) }
        binding.btnReportDialogSubmit.setOnClickListener {
            reportReason?.let {
                reportDialogClickListener.submitReport(it, binding.etReportDialogDetail.text.toString())
            } ?: showReportOption()
        }
    }

    private fun showReportDetail(reportReason: ReportReason, head: String) {
        this.reportReason = reportReason
        binding.tvReportDialogDetailHead.text = head
        binding.llReportDialogOptionWrapper.slideOutToLeft()
        binding.llReportDialogDetailWrapper.slideInFromRight()
    }

    private fun showReportOption() {
        requireContext().hideKeyboard(requireView())
        binding.llReportDialogDetailWrapper.slideOutToRight()
        binding.llReportDialogOptionWrapper.slideInFromLeft()
    }

    fun showLoading() {
        binding.llReportDialogLoading.visibility = View.VISIBLE
    }

    fun hideLoading() {
        binding.llReportDialogLoading.visibility = View.GONE
    }

    fun clickSubmitButton() {
        binding.btnReportDialogSubmit.performClick()
    }

    companion object {
        const val TAG = "reportMenuDialog"
    }

    interface ReportDialogClickListener {
        fun submitReport(reportReason: ReportReason, description: String)
    }
}