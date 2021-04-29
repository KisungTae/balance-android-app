package com.beeswork.balance.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import com.beeswork.balance.R
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
        binding.btnReportMenuDialogClose.setOnClickListener { dismiss() }
        binding.btnReportMenuDialogInappropriateMessage.setOnClickListener {
            println("clicked btnReportMenuDialogInappropriateMessage")

//            val transition: Transition = Slide(Gravity.START)
//            transition.duration = 600
//            transition.addTarget(binding.llTest)
//
//            TransitionManager.beginDelayedTransition(binding.llReportMenuDialogWrapper, transition);
            val animation = TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0f,
                Animation.RELATIVE_TO_PARENT, 0.80f,
                Animation.RELATIVE_TO_PARENT, 0f,
                Animation.RELATIVE_TO_PARENT, 0f
            )



            animation.duration = 1000
            animation.isFillEnabled = true
            animation.fillAfter = true

            binding.llTest.startAnimation(animation)

        }

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