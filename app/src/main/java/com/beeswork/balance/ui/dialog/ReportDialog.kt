package com.beeswork.balance.ui.dialog

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogReportMenuBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.math.abs


class ReportDialog(
    private val reportMenuDialogClickListener: ReportMenuDialogClickListener
) : BottomSheetDialogFragment() {

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
        binding.btnReportMenuDialogInappropriateBehaviour.setOnClickListener { showDetail() }
        binding.btnReportMenuDialogInappropriateMessage.setOnClickListener { showDetail() }
        binding.btnReportMenuSubmit.setOnClickListener { hideDetail() }
        binding.btnReportMenuDialogClose.setOnClickListener { hideDetail() }
    }

    private fun showDetail() {
        binding.llReportMenuDialogOptionWrapper.animate()
            .setDuration(ANIMATION_DURATION)
            .translationX(binding.llReportMenuDialogOptionWrapper.width.toFloat().unaryMinus())
            .start()
        binding.llReportMenuDialogDetailWrapper.translationX = binding.llReportMenuDialogDetailWrapper.width.toFloat()
        binding.llReportMenuDialogDetailWrapper.animate().setDuration(ANIMATION_DURATION).translationX(0f).start()
    }

    private fun hideDetail() {
        binding.llReportMenuDialogOptionWrapper.animate().setDuration(ANIMATION_DURATION).translationX(0f).start()
        binding.llReportMenuDialogDetailWrapper.animate()
            .setDuration(ANIMATION_DURATION)
            .translationX(binding.llReportMenuDialogDetailWrapper.width.toFloat())
            .start()
    }

    companion object {
        const val TAG = "reportMenuDialog"
        const val ANIMATION_DURATION = 200L
    }

    interface ReportMenuDialogClickListener {

    }
}