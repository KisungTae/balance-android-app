package com.beeswork.balance.ui.reportdialog


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogReportBinding
import com.beeswork.balance.internal.constant.ReportReason
import com.beeswork.balance.internal.constant.ReportType
import com.beeswork.balance.internal.util.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import java.util.*


class ReportDialog(
    private val reportDialogListener: ReportDialogListener,
    private val reportType: ReportType,
    private val reportedId: UUID
) : BottomSheetDialogFragment(), KodeinAware {

    override val kodein by closestKodein()
    private val viewModelFactory: ReportViewModelFactory by instance()
    private lateinit var viewModel: ReportViewModel
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
        viewModel = ViewModelProvider(this, viewModelFactory).get(ReportViewModel::class.java)
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
        observeReportUIStateLiveData()
        setupBtnListeners()
    }

    private fun setupBtnListeners() {
        binding.btnReportDialogClose.setOnClickListener { dismiss() }
        binding.btnReportDialogErrorClose.setOnClickListener { dismiss() }
        binding.btnReportDialogSuccessClose.setOnClickListener {
            reportDialogListener.onReportSubmitted()
            dismiss()
        }
        binding.btnReportDialogDescriptionBack.setOnClickListener { goBackToReportOption() }
        binding.btnReportDialogErrorBack.setOnClickListener { goBackToReportDetail() }
        binding.btnReportDialogMessage.setOnClickListener { showReportDetail(ReportReason.MESSAGE, it.getText()) }
        binding.btnReportDialogBehaviour.setOnClickListener { showReportDetail(ReportReason.BEHAVIOUR, it.getText()) }
        binding.btnReportDialogSpam.setOnClickListener { showReportDetail(ReportReason.SPAM, it.getText()) }
        binding.btnReportDialogPhoto.setOnClickListener { showReportDetail(ReportReason.PHOTO, it.getText()) }
        binding.btnReportDialogOther.setOnClickListener { showReportDetail(ReportReason.OTHER, it.getText()) }
        binding.btnReportDialogRetry.setOnClickListener { submitReport() }
        binding.btnReportDialogSubmit.setOnClickListener {
            showResultWrapper()
            submitReport()
        }
    }

    private fun submitReport() {
        val reportDescription = binding.etReportDialogDescription.text.toString()
        viewModel.report(reportType, reportedId, reportReason!!, reportDescription)
    }

    private fun goBackToReportOption() {
        requireContext().hideKeyboard(requireView())
        binding.llReportDialogDescriptionWrapper.slideOutToRight()
        binding.llReportDialogOptionWrapper.slideInFromLeft()
    }

    private fun goBackToReportDetail() {
        binding.flReportDialogResultWrapper.slideOutToRight()
        binding.llReportDialogDescriptionWrapper.slideInFromLeft()
    }


    private fun observeReportUIStateLiveData() {
        viewModel.reportUIStateLiveData.observe(viewLifecycleOwner) { reportUIState ->
            when {
                reportUIState.reported -> {
                    updateLayouts(View.VISIBLE, View.GONE, View.GONE)
                }
                reportUIState.showLoading -> {
                    updateLayouts(View.GONE, View.VISIBLE, View.GONE)
                }
                reportUIState.showError -> {
                    requireContext().hideKeyboard(requireView())
                    binding.tvReportDialogErrorTitle.text = getString(R.string.error_title_report)
                    binding.tvReportDialogErrorMessage.text = MessageSource.getMessage(requireContext(), reportUIState.exception)
                    updateLayouts(View.GONE, View.GONE, View.VISIBLE)
                }
            }
        }
    }

    private fun updateLayouts(success: Int, loading: Int, error: Int) {
        binding.llReportDialogSuccessWrapper.visibility = success
        binding.llReportDialogLoadingWrapper.visibility = loading
        binding.llReportDialogErrorWrapper.visibility = error
    }

    private fun showReportDetail(reportReason: ReportReason, head: String) {
        this.reportReason = reportReason
        binding.tvReportDialogDescriptionHead.text = head
        binding.llReportDialogOptionWrapper.slideOutToLeft()
        binding.llReportDialogDescriptionWrapper.slideInFromRight()
    }

    private fun showResultWrapper() {
        requireContext().hideKeyboard(requireView())
        binding.llReportDialogDescriptionWrapper.slideOutToLeft()
        binding.flReportDialogResultWrapper.slideInFromRight()
    }

    companion object {
        const val TAG = "reportMenuDialog"
    }

    interface ReportDialogListener {
        fun onReportSubmitted()
    }
}