package com.beeswork.balance.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogNewMatchBinding
import java.util.*

class NewMatchDialog(
    private val matchedId: UUID,
    private val matchedName: String,
    private val matchedRepPhotoKey: String?,
    private val accountId: UUID?,
    private val repPhotoKey: String?
): DialogFragment() {

    private lateinit var binding: DialogNewMatchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogNewMatchBinding.inflate(layoutInflater)
        return binding.root
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