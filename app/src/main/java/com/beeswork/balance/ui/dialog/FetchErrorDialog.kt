package com.beeswork.balance.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.beeswork.balance.R
import com.beeswork.balance.databinding.DialogFetchErrorBinding

class FetchErrorDialog(
    private val errorMessage: String?,
    private val onRetryListener: OnRetryListener
): BaseErrorDialog() {

    private lateinit var binding: DialogFetchErrorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_FullScreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogFetchErrorBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnFetchErrorClose.setOnClickListener { dismiss() }
        binding.btnRefetch.setOnClickListener {
            dismiss()
            onRetryListener.onRetry()
        }
        binding.tvFetchErrorMessage.text = errorMessage
    }

    companion object {
        const val TAG = "fetchErrorDialog"
    }

    interface OnRetryListener {
        fun onRetry()
    }
}