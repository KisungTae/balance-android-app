package com.beeswork.balance.ui.dialog


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogReportBinding
import com.beeswork.balance.internal.util.hideKeyboard
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class ReportDialog(
    private val reportMenuDialogClickListener: ReportMenuDialogClickListener
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
        binding.btnReportDialogInappropriateMessage.setOnClickListener {
            showReportDetail(ReportReason.INAPPROPRIATE_MESSAGE, (it as Button).text.toString())
        }
        binding.btnReportDialogInappropriateBehaviour.setOnClickListener {
            showReportDetail(ReportReason.INAPPROPRIATE_BEHAVIOUR, (it as Button).text.toString())
        }
        binding.btnReportDialogSpam.setOnClickListener {
            showReportDetail(ReportReason.SPAM, (it as Button).text.toString())
        }
        binding.btnReportDialogInappropriatePhoto.setOnClickListener {
            showReportDetail(ReportReason.INAPPROPRIATE_PHOTO, (it as Button).text.toString())
        }
        binding.btnReportDialogOther.setOnClickListener {
            showReportDetail(ReportReason.OTHER, (it as Button).text.toString())
        }
        binding.btnReportDialogSubmit.setOnClickListener {
            reportReason?.let {
                reportMenuDialogClickListener.submitReport(it.ordinal, binding.etReportDialogDetail.text.toString())
            } ?: showReportOption()
        }
    }

    private fun showReportDetail(reportReason: ReportReason, head: String) {
        this.reportReason = reportReason
        binding.tvReportDialogDetailHead.text = head
        binding.llReportDialogOptionWrapper.animate()
            .setDuration(ANIMATION_DURATION)
            .withEndAction { binding.llReportDialogOptionWrapper.visibility = View.GONE }
            .translationX(binding.llReportDialogOptionWrapper.width.toFloat().unaryMinus())
            .start()
        binding.llReportDialogDetailWrapper.translationX = binding.llReportDialogDetailWrapper.width.toFloat()
        binding.llReportDialogDetailWrapper.animate().setDuration(ANIMATION_DURATION).translationX(0f).start()
    }

    private fun showReportOption() {
        binding.llReportDialogOptionWrapper.animate()
            .withStartAction { binding.llReportDialogOptionWrapper.visibility = View.VISIBLE }
            .setDuration(ANIMATION_DURATION)
            .translationX(0f)
            .start()
        binding.llReportDialogDetailWrapper.animate()
            .setDuration(ANIMATION_DURATION)
            .translationX(binding.llReportDialogDetailWrapper.width.toFloat())
            .withEndAction { requireContext().hideKeyboard(requireView()) }
            .start()
    }

    companion object {
        const val TAG = "reportMenuDialog"
        const val ANIMATION_DURATION = 100L
    }

    enum class ReportReason {
        NOTHING,
        INAPPROPRIATE_MESSAGE,
        INAPPROPRIATE_PHOTO,
        SPAM,
        INAPPROPRIATE_BEHAVIOUR,
        OTHER
    }

    interface ReportMenuDialogClickListener {
        fun submitReport(reportReasonId: Int, description: String)
    }
}