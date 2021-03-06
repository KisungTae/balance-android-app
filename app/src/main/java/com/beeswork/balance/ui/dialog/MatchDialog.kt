package com.beeswork.balance.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogMatchedBinding

class MatchDialog(
    private val matcherPhotoKey: String?,
    private val matchedPhotoKey: String?
): DialogFragment() {

    private lateinit var binding: DialogMatchedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen)
        binding = DialogMatchedBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_matched, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnMatchDialogClose.setOnClickListener { dismiss() }
        binding.btnMatchDialogGoToChat.setOnClickListener {
            // go to chat

            dismiss()
        }
    }

    companion object {
        const val TAG = "matchDialog"
    }
}