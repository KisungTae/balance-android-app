package com.beeswork.balance.ui.chatfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogChatMoreMenuBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ChatMoreMenuDialog(
    private val chatMoreMenuDialogClickListener: ChatMoreMenuDialogClickListener
) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogChatMoreMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogChatMoreMenuBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        binding.btnChatMoreMenuDialogReport.setOnClickListener {
            dismiss()
            chatMoreMenuDialogClickListener.onReportMatch()
        }
        binding.btnChatMoreMenuDialogUnmatch.setOnClickListener {
            dismiss()
            chatMoreMenuDialogClickListener.onUnmatch()
        }
        binding.btnChatMoreMenuDialogClose.setOnClickListener { dismiss() }
    }

    interface ChatMoreMenuDialogClickListener {
        fun onUnmatch()
        fun onReportMatch()
    }

    companion object {
        const val TAG = "chatMoreDialog"
    }
}